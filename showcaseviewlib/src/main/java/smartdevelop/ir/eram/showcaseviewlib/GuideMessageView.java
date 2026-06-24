package smartdevelop.ir.eram.showcaseviewlib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 */
class GuideMessageView extends FrameLayout {

    private static final int RADIUS_SIZE = 5;
    private static final int PADDING_SIZE = 10;
    private static final int BOTTOM_PADDING_SIZE = 5;
    private static final int DEFAULT_TITLE_TEXT_SIZE = 18;
    private static final int DEFAULT_CONTENT_TEXT_SIZE = 14;
    private static final int CLOSE_BUTTON_SIZE = 40;
    private static final int CLOSE_BUTTON_TEXT_SIZE = 18;
    private static final int TITLE_SKIP_MIN_SPACING = 46;
    private static final int CLOSE_BUTTON_MAX_WIDTH = 96;
    private static final int CLOSE_BUTTON_HORIZONTAL_PADDING = 8;
    private static final int OUTLINED_BUTTON_STROKE_WIDTH = 1;
    private static final int OUTLINED_BUTTON_RADIUS = 4;
    private static final int HEADER_SEPARATOR_HEIGHT = 1;
    private static final int HEADER_SEPARATOR_COLOR = 0x1F000000;
    private static final int CLOSE_BUTTON_ICON_COLOR = 0x99000000;
    private static final int CLOSE_BUTTON_PRESSED_COLOR = 0x14000000;

    private final Paint mPaint;
    private final RectF mRect;
    private final LinearLayout mTextContainer;
    private final FrameLayout mHeaderContainer;
    private final TextView mTitleTextView;
    private final TextView mContentTextView;
    private final TextView mCloseButton;
    private final View mHeaderSeparator;
    private Drawable customBackgroundDrawable;

    private final int[] location = new int[2];
    private final int contentPadding;
    private final int titleBottomPadding;
    private final int closeButtonSize;
    private final int titleSkipMinSpacing;
    private final int closeButtonMaxWidth;
    private final int closeButtonHorizontalPadding;
    private final int outlinedButtonStrokeWidth;
    private final int outlinedButtonRadius;
    private final int headerSeparatorHeight;
    private boolean hasTitle = true;
    private boolean skipButtonOutlined;
    private boolean hasCustomSkipButtonBackground;

    GuideMessageView(Context context) {
        super(context);

        float density = context.getResources().getDisplayMetrics().density;
        contentPadding = (int) (PADDING_SIZE * density);
        titleBottomPadding = (int) (BOTTOM_PADDING_SIZE * density);
        closeButtonSize = (int) (CLOSE_BUTTON_SIZE * density);
        titleSkipMinSpacing = (int) (TITLE_SKIP_MIN_SPACING * density);
        closeButtonMaxWidth = (int) (CLOSE_BUTTON_MAX_WIDTH * density);
        closeButtonHorizontalPadding = (int) (CLOSE_BUTTON_HORIZONTAL_PADDING * density);
        outlinedButtonStrokeWidth = Math.max(1, (int) (OUTLINED_BUTTON_STROKE_WIDTH * density));
        outlinedButtonRadius = (int) (OUTLINED_BUTTON_RADIUS * density);
        headerSeparatorHeight = Math.max(1, (int) (HEADER_SEPARATOR_HEIGHT * density));

        setWillNotDraw(false);

        mRect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextContainer = new LinearLayout(context);
        mTextContainer.setOrientation(LinearLayout.VERTICAL);
        mTextContainer.setGravity(Gravity.CENTER);
        updateTextContainerPadding();
        addView(
            mTextContainer,
            new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );

        mHeaderContainer = new FrameLayout(context);
        mTextContainer.addView(
            mHeaderContainer,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );

        mTitleTextView = new TextView(context);
        mTitleTextView.setPadding(0, 0, 0, 0);
        mTitleTextView.setGravity(startGravity() | Gravity.CENTER_VERTICAL);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TITLE_TEXT_SIZE);
        mTitleTextView.setTextColor(Color.BLACK);
        mTitleTextView.setEllipsize(null);
        mHeaderContainer.addView(
            mTitleTextView,
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                startGravity() | Gravity.CENTER_VERTICAL
            )
        );

        mCloseButton = new TextView(context);
        mCloseButton.setText("\u2715");
        mCloseButton.setTextColor(CLOSE_BUTTON_ICON_COLOR);
        mCloseButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, CLOSE_BUTTON_TEXT_SIZE);
        mCloseButton.setGravity(Gravity.CENTER);
        mCloseButton.setSingleLine(true);
        mCloseButton.setEllipsize(TextUtils.TruncateAt.END);
        mCloseButton.setMinWidth(closeButtonSize);
        mCloseButton.setMinHeight(closeButtonSize);
        mCloseButton.setMaxWidth(closeButtonMaxWidth);
        mCloseButton.setPadding(
            closeButtonHorizontalPadding,
            0,
            closeButtonHorizontalPadding,
            0
        );
        mCloseButton.setContentDescription(
            context.getString(R.string.guide_skip_content_description)
        );
        mCloseButton.setBackgroundDrawable(createDefaultSkipButtonBackground());
        mCloseButton.setVisibility(View.GONE);
        FrameLayout.LayoutParams closeButtonParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            closeButtonSize,
            endGravity() | Gravity.CENTER_VERTICAL
        );
        mHeaderContainer.addView(mCloseButton, closeButtonParams);

        mHeaderSeparator = new View(context);
        mHeaderSeparator.setBackgroundColor(HEADER_SEPARATOR_COLOR);
        mTextContainer.addView(
            mHeaderSeparator,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                headerSeparatorHeight
            )
        );

        mContentTextView = new TextView(context);
        mContentTextView.setTextColor(Color.BLACK);
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTENT_TEXT_SIZE);
        mContentTextView.setPadding(0, titleBottomPadding, 0, 0);
        mContentTextView.setGravity(Gravity.CENTER);
        mTextContainer.addView(
            mContentTextView,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );

        updateHeaderVisibility();
    }

    public void setTitle(String title) {
        if (title == null) {
            if (hasTitle) {
                hasTitle = false;
                updateHeaderVisibility();
            }
            return;
        }
        hasTitle = true;
        mTitleTextView.setText(title);
        updateHeaderVisibility();
    }

    public void setContentText(String content) {
        mContentTextView.setText(content);
    }

    public void setContentSpan(Spannable content) {
        mContentTextView.setText(content);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mContentTextView.setTypeface(typeFace);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mTitleTextView.setTypeface(typeFace);
    }

    public void setTypeFace(Typeface typeFace) {
        setTitleTypeFace(typeFace);
        setContentTypeFace(typeFace);
    }

    public void setTitleTextColor(int color) {
        mTitleTextView.setTextColor(color);
    }

    public void setContentTextColor(int color) {
        mContentTextView.setTextColor(color);
    }

    public void setSkipButtonTextColor(int color) {
        mCloseButton.setTextColor(color);
        updateSkipButtonBackground();
    }

    public void setTitleTextStyle(int style) {
        mTitleTextView.setTypeface(mTitleTextView.getTypeface(), style);
    }

    public void setContentTextStyle(int style) {
        mContentTextView.setTypeface(mContentTextView.getTypeface(), style);
    }

    public void setSkipButtonText(CharSequence text) {
        mCloseButton.setText(text);
        updateSkipButtonBackground();
    }

    public void setTitleTextSize(int size) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setContentTextSize(int size) {
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setCustomBackgroundDrawable(Drawable drawable) {
        customBackgroundDrawable = drawable;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setSkipButtonBackgroundDrawable(Drawable drawable) {
        hasCustomSkipButtonBackground = drawable != null;
        mCloseButton.setBackgroundDrawable(drawable != null ? drawable : createDefaultSkipButtonBackground());
        updateSkipButtonBackground();
    }

    public void setSkipButtonOutlined(boolean outlined) {
        skipButtonOutlined = outlined;
        updateSkipButtonBackground();
    }

    public void setSkipButtonVisible(boolean visible) {
        mCloseButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        updateHeaderVisibility();
    }

    public void setOnSkipClickListener(OnClickListener listener) {
        mCloseButton.setOnClickListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        prepareHeaderForMeasure(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderContainer.getVisibility() == View.VISIBLE) {
            int headerWidth = resolveHeaderWidth(widthMeasureSpec);
            applyHeaderWidth(headerWidth);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getLocationOnScreen(location);

        mRect.set(
            getPaddingLeft(),
            getPaddingTop(),
            getWidth() - getPaddingRight(),
            getHeight() - getPaddingBottom()
        );

        if (customBackgroundDrawable != null) {
            customBackgroundDrawable.setBounds(
                (int) mRect.left,
                (int) mRect.top,
                (int) mRect.right,
                (int) mRect.bottom
            );
            customBackgroundDrawable.draw(canvas);
            return;
        }

        final int density = (int) getResources().getDisplayMetrics().density;
        final int radiusSize = RADIUS_SIZE * density;
        canvas.drawRoundRect(mRect, radiusSize, radiusSize, mPaint);
    }

    private void updateHeaderVisibility() {
        mTitleTextView.setVisibility(hasTitle ? View.VISIBLE : View.GONE);
        mHeaderContainer.setVisibility(
            hasTitle || mCloseButton.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE
        );
        mHeaderSeparator.setVisibility(mHeaderContainer.getVisibility());
        int contentTopPadding = mHeaderContainer.getVisibility() == View.VISIBLE
            ? titleBottomPadding
            : 0;
        mContentTextView.setPadding(0, contentTopPadding, 0, 0);
    }

    private void prepareHeaderForMeasure(int widthMeasureSpec) {
        int availableWidth = getAvailableTextWidth(widthMeasureSpec);
        if (availableWidth <= 0) {
            mTitleTextView.setMaxWidth(Integer.MAX_VALUE);
            mContentTextView.setMaxWidth(Integer.MAX_VALUE);
            mCloseButton.setMaxWidth(closeButtonMaxWidth);
            return;
        }

        mContentTextView.setMaxWidth(availableWidth);
        mCloseButton.setMaxWidth(Math.min(closeButtonMaxWidth, Math.max(closeButtonSize, availableWidth / 2)));
        mTitleTextView.setMaxWidth(availableWidth);
    }

    private int resolveHeaderWidth(int widthMeasureSpec) {
        int availableWidth = getAvailableTextWidth(widthMeasureSpec);
        int contentWidth = mContentTextView.getMeasuredWidth();
        int titleWidth = hasTitle ? mTitleTextView.getMeasuredWidth() : 0;
        int skipWidth = mCloseButton.getVisibility() == View.VISIBLE ? mCloseButton.getMeasuredWidth() : 0;
        int spacingWidth = titleWidth > 0 && skipWidth > 0 ? titleSkipMinSpacing : 0;
        int desiredHeaderWidth = titleWidth + spacingWidth + skipWidth;

        int headerWidth = Math.max(contentWidth, desiredHeaderWidth);
        if (availableWidth > 0) {
            headerWidth = Math.min(headerWidth, availableWidth);
        }
        return Math.max(0, headerWidth);
    }

    private void applyHeaderWidth(int headerWidth) {
        mHeaderContainer.setMinimumWidth(headerWidth);
        ViewGroup.LayoutParams params = mHeaderContainer.getLayoutParams();
        if (params.width != headerWidth) {
            params.width = headerWidth;
            mHeaderContainer.setLayoutParams(params);
        }

        int skipWidth = mCloseButton.getVisibility() == View.VISIBLE ? mCloseButton.getMeasuredWidth() : 0;
        int reservedSpace = skipWidth > 0 && hasTitle ? skipWidth + titleSkipMinSpacing : 0;
        mTitleTextView.setMaxWidth(Math.max(0, headerWidth - reservedSpace));
    }

    private int getAvailableTextWidth(int widthMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return 0;
        }
        return MeasureSpec.getSize(widthMeasureSpec)
            - getPaddingLeft()
            - getPaddingRight()
            - mTextContainer.getPaddingLeft()
            - mTextContainer.getPaddingRight();
    }

    private void updateTextContainerPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTextContainer.setPaddingRelative(
                contentPadding,
                contentPadding,
                contentPadding,
                contentPadding
            );
        } else {
            mTextContainer.setPadding(
                contentPadding,
                contentPadding,
                contentPadding,
                contentPadding
            );
        }
    }

    private int startGravity() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
            ? Gravity.START
            : Gravity.LEFT;
    }

    private int endGravity() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
            ? Gravity.END
            : Gravity.RIGHT;
    }

    @SuppressWarnings("deprecation")
    private void updateSkipButtonBackground() {
        if (hasCustomSkipButtonBackground) {
            return;
        }

        mCloseButton.setBackgroundDrawable(
            skipButtonOutlined && isTextSkipButton()
                ? createOutlinedSkipButtonBackground()
                : createDefaultSkipButtonBackground()
        );
    }

    private boolean isTextSkipButton() {
        CharSequence text = mCloseButton.getText();
        return text != null && text.length() > 1;
    }

    private StateListDrawable createDefaultSkipButtonBackground() {
        StateListDrawable states = new StateListDrawable();
        states.addState(
            new int[]{android.R.attr.state_pressed},
            createCloseButtonCircle(CLOSE_BUTTON_PRESSED_COLOR)
        );
        states.addState(new int[]{}, createCloseButtonCircle(Color.TRANSPARENT));
        return states;
    }

    private StateListDrawable createOutlinedSkipButtonBackground() {
        StateListDrawable states = new StateListDrawable();
        states.addState(
            new int[]{android.R.attr.state_pressed},
            createOutlinedSkipButtonDrawable(CLOSE_BUTTON_PRESSED_COLOR)
        );
        states.addState(new int[]{}, createOutlinedSkipButtonDrawable(Color.TRANSPARENT));
        return states;
    }

    private GradientDrawable createOutlinedSkipButtonDrawable(int fillColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(outlinedButtonRadius);
        drawable.setStroke(outlinedButtonStrokeWidth, mCloseButton.getCurrentTextColor());
        return drawable;
    }

    private GradientDrawable createCloseButtonCircle(int fillColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(fillColor);
        float density = Resources.getSystem().getDisplayMetrics().density;
        drawable.setSize((int) (CLOSE_BUTTON_SIZE * density), (int) (CLOSE_BUTTON_SIZE * density));
        return drawable;
    }
}
