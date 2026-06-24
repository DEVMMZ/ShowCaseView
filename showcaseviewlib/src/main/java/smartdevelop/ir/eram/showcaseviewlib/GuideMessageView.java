package smartdevelop.ir.eram.showcaseviewlib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    private static final int CLOSE_BUTTON_MARGIN = 8;
    private static final int CLOSE_BUTTON_ICON_COLOR = 0x99000000;
    private static final int CLOSE_BUTTON_PRESSED_COLOR = 0x14000000;

    private final Paint mPaint;
    private final RectF mRect;
    private final LinearLayout mTextContainer;
    private final TextView mTitleTextView;
    private final TextView mContentTextView;
    private final TextView mCloseButton;

    private final int[] location = new int[2];
    private final int contentPadding;
    private final int titleBottomPadding;
    private final int closeButtonSize;
    private final int closeButtonMargin;
    private boolean hasTitle = true;

    GuideMessageView(Context context) {
        super(context);

        float density = context.getResources().getDisplayMetrics().density;
        contentPadding = (int) (PADDING_SIZE * density);
        titleBottomPadding = (int) (BOTTOM_PADDING_SIZE * density);
        closeButtonSize = (int) (CLOSE_BUTTON_SIZE * density);
        closeButtonMargin = (int) (CLOSE_BUTTON_MARGIN * density);

        setWillNotDraw(false);

        mRect = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextContainer = new LinearLayout(context);
        mTextContainer.setOrientation(LinearLayout.VERTICAL);
        mTextContainer.setGravity(Gravity.CENTER);
        updateTextContainerPadding(false);
        addView(
            mTextContainer,
            new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );

        mTitleTextView = new TextView(context);
        mTitleTextView.setPadding(
            contentPadding,
            contentPadding,
            contentPadding,
            titleBottomPadding
        );
        mTitleTextView.setGravity(Gravity.CENTER);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TITLE_TEXT_SIZE);
        mTitleTextView.setTextColor(Color.BLACK);
        mTextContainer.addView(
            mTitleTextView,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );

        mContentTextView = new TextView(context);
        mContentTextView.setTextColor(Color.BLACK);
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTENT_TEXT_SIZE);
        mContentTextView.setPadding(
            contentPadding,
            titleBottomPadding,
            contentPadding,
            contentPadding
        );
        mContentTextView.setGravity(Gravity.CENTER);
        mTextContainer.addView(
            mContentTextView,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        LayoutParams closeLayoutParams = new LayoutParams(closeButtonSize, closeButtonSize);
        closeLayoutParams.gravity = Gravity.TOP | Gravity.END;
        closeLayoutParams.setMargins(
            closeButtonMargin,
            closeButtonMargin,
            closeButtonMargin,
            closeButtonMargin
        );
        addView(mCloseButton, closeLayoutParams);
    }

    public void setTitle(String title) {
        if (title == null) {
            if (hasTitle) {
                mTextContainer.removeView(mTitleTextView);
                hasTitle = false;
            }
            return;
        }
        if (!hasTitle) {
            mTextContainer.addView(mTitleTextView, 0);
            hasTitle = true;
        }
        mTitleTextView.setText(title);
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

    public void setSkipButtonVisible(boolean visible) {
        mCloseButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        updateTextContainerPadding(visible);
    }

    public void setOnSkipClickListener(OnClickListener listener) {
        mCloseButton.setOnClickListener(listener);
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

        final int density = (int) getResources().getDisplayMetrics().density;
        final int radiusSize = RADIUS_SIZE * density;
        canvas.drawRoundRect(mRect, radiusSize, radiusSize, mPaint);
    }

    private void updateTextContainerPadding(boolean hasCloseButton) {
        int endPadding = contentPadding;
        if (hasCloseButton) {
            endPadding += closeButtonSize + (closeButtonMargin * 2);
        }
        int start = contentPadding;
        int top = contentPadding;
        int bottom = contentPadding;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mTextContainer.setPaddingRelative(start, top, endPadding, bottom);
        } else {
            mTextContainer.setPadding(start, top, endPadding, bottom);
        }
    }

    @SuppressWarnings("deprecation")
    private StateListDrawable createCloseButtonBackground() {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, createCloseButtonCircle(CLOSE_BUTTON_PRESSED_COLOR));
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
