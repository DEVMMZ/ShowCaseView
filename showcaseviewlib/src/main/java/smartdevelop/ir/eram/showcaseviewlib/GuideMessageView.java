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
    private static final int CLOSE_BUTTON_SPACING = 8;
    private static final int CLOSE_BUTTON_ICON_COLOR = 0x99000000;
    private static final int CLOSE_BUTTON_PRESSED_COLOR = 0x14000000;

    private final Paint mPaint;
    private final RectF mRect;
    private final LinearLayout mTextContainer;
    private final FrameLayout mHeaderContainer;
    private final TextView mTitleTextView;
    private final TextView mContentTextView;
    private final TextView mCloseButton;
    private Drawable customBackgroundDrawable;

    private final int[] location = new int[2];
    private final int contentPadding;
    private final int titleBottomPadding;
    private final int closeButtonSize;
    private final int closeButtonSpacing;
    private boolean hasTitle = true;

    GuideMessageView(Context context) {
        super(context);

        float density = context.getResources().getDisplayMetrics().density;
        contentPadding = (int) (PADDING_SIZE * density);
        titleBottomPadding = (int) (BOTTOM_PADDING_SIZE * density);
        closeButtonSize = (int) (CLOSE_BUTTON_SIZE * density);
        closeButtonSpacing = (int) (CLOSE_BUTTON_SPACING * density);

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
        mTitleTextView.setGravity(Gravity.CENTER);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TITLE_TEXT_SIZE);
        mTitleTextView.setTextColor(Color.BLACK);
        mHeaderContainer.addView(
            mTitleTextView,
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        );

        mCloseButton = new TextView(context);
        mCloseButton.setText("\u2715");
        mCloseButton.setTextColor(CLOSE_BUTTON_ICON_COLOR);
        mCloseButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, CLOSE_BUTTON_TEXT_SIZE);
        mCloseButton.setGravity(Gravity.CENTER);
        mCloseButton.setContentDescription(
            context.getString(R.string.guide_skip_content_description)
        );
        mCloseButton.setBackgroundDrawable(createCloseButtonBackground());
        mCloseButton.setVisibility(View.GONE);
        FrameLayout.LayoutParams closeButtonParams = new FrameLayout.LayoutParams(
            closeButtonSize,
            closeButtonSize,
            Gravity.CENTER_VERTICAL | Gravity.END
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            closeButtonParams.setMarginEnd(closeButtonSpacing);
        } else {
            closeButtonParams.rightMargin = closeButtonSpacing;
        }
        mHeaderContainer.addView(mCloseButton, closeButtonParams);

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
    }

    public void setTitleTextStyle(int style) {
        mTitleTextView.setTypeface(mTitleTextView.getTypeface(), style);
    }

    public void setContentTextStyle(int style) {
        mContentTextView.setTypeface(mContentTextView.getTypeface(), style);
    }

    public void setSkipButtonText(CharSequence text) {
        mCloseButton.setText(text);
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
        mCloseButton.setBackgroundDrawable(
            drawable != null ? drawable : createCloseButtonBackground()
        );
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
        updateTitleInsets();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeaderContainer.getVisibility() != View.VISIBLE) {
            return;
        }

        int targetHeaderWidth = Math.max(
            mHeaderContainer.getMeasuredWidth(),
            mContentTextView.getMeasuredWidth()
        );
        int targetHeaderHeight = mHeaderContainer.getMeasuredHeight();
        updateTitleMaxWidth(targetHeaderWidth);
        if (mHeaderContainer.getMeasuredWidth() != targetHeaderWidth) {
            mHeaderContainer.measure(
                MeasureSpec.makeMeasureSpec(targetHeaderWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(targetHeaderHeight, MeasureSpec.EXACTLY)
            );
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
        updateTitleInsets();
        int contentTopPadding = mHeaderContainer.getVisibility() == View.VISIBLE
            ? titleBottomPadding
            : 0;
        mContentTextView.setPadding(0, contentTopPadding, 0, 0);
    }

    private void updateTitleInsets() {
        int titleInset = mCloseButton.getVisibility() == View.VISIBLE
            ? closeButtonSize + closeButtonSpacing
            : 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTitleTextView.setPaddingRelative(titleInset, 0, titleInset, 0);
        } else {
            mTitleTextView.setPadding(titleInset, 0, titleInset, 0);
        }
    }

    private void updateTitleMaxWidth(int headerWidth) {
        int titleInset = mCloseButton.getVisibility() == View.VISIBLE
            ? closeButtonSize + closeButtonSpacing
            : 0;
        mTitleTextView.setMaxWidth(Math.max(0, headerWidth - (titleInset * 2)));
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

    @SuppressWarnings("deprecation")
    private StateListDrawable createCloseButtonBackground() {
        StateListDrawable states = new StateListDrawable();
        states.addState(
            new int[]{android.R.attr.state_pressed},
            createCloseButtonCircle(CLOSE_BUTTON_PRESSED_COLOR)
        );
        states.addState(new int[]{}, createCloseButtonCircle(Color.TRANSPARENT));
        return states;
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
