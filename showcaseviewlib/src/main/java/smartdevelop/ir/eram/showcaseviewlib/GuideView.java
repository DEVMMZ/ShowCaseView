package smartdevelop.ir.eram.showcaseviewlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.Xfermode;
import android.os.Build;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;
import smartdevelop.ir.eram.showcaseviewlib.listener.SkipListener;

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 */

@SuppressLint("ViewConstructor")
public class GuideView extends FrameLayout {

    private static final int INDICATOR_HEIGHT = 40;
    private static final int MESSAGE_VIEW_PADDING = 5;
    private static final int SIZE_ANIMATION_DURATION = 700;
    private static final int APPEARING_ANIMATION_DURATION = 400;
    private static final int CIRCLE_INDICATOR_SIZE = 6;
    private static final int LINE_INDICATOR_WIDTH_SIZE = 3;
    private static final int STROKE_CIRCLE_INDICATOR_SIZE = 3;
    private static final int RADIUS_SIZE_TARGET_RECT = 15;
    private static final int MARGIN_INDICATOR = 15;

    private static final int BACKGROUND_COLOR = 0x99000000;
    private static final int CIRCLE_INNER_INDICATOR_COLOR = 0xffcccccc;
    private static final int CIRCLE_INDICATOR_COLOR = Color.WHITE;
    private static final int LINE_INDICATOR_COLOR = Color.WHITE;

    private final Paint selfPaint = new Paint();
    private final Paint paintLine = new Paint();
    private final Paint paintCircle = new Paint();
    private final Paint paintCircleInner = new Paint();
    private final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Xfermode X_FER_MODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private final Path arrowPath = new Path();

    private final View target;
    private RectF targetRect;
    private final Rect backgroundRect = new Rect();
    private final Rect safeAreaRect = new Rect();
    private final int[] guideLocation = new int[2];
    private final int[] targetLocation = new int[2];

    private final float density;
    private float stopY;
    private boolean isTop;
    private boolean mIsShowing;
    private int yMessageView = 0;

    private float startYLineAndCircle;
    private float circleIndicatorSize = 0;
    private float circleIndicatorSizeFinal;
    private float circleInnerIndicatorSize = 0;
    private float circleInnerIndicatorSizeFinal;
    private float lineIndicatorWidthSize;
    private int messageViewPadding;
    private float indicatorMargin;
    private float strokeCircleWidth;
    private float indicatorHeight;

    private boolean isPerformedAnimationSize = false;

    private GuideListener mGuideListener;
    private SkipListener mSkipListener;
    private Gravity mGravity;
    private DismissType dismissType;
    private PointerType pointerType;
    private final GuideMessageView mMessageView;
    private boolean showSkipButton;
    private int overlayColor = BACKGROUND_COLOR;
    private int lineIndicatorColor = LINE_INDICATOR_COLOR;
    private int circleIndicatorColor = CIRCLE_INDICATOR_COLOR;
    private int circleInnerIndicatorColor = CIRCLE_INNER_INDICATOR_COLOR;
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            updateGuideLayout(false);
        }
    };

    private GuideView(Context context, View view) {
        super(context);
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.target = view;
        density = context.getResources().getDisplayMetrics().density;
        init();

        mMessageView = new GuideMessageView(getContext());
        mMessageView.setPadding(
            messageViewPadding,
            messageViewPadding,
            messageViewPadding,
            messageViewPadding
        );
        mMessageView.setColor(Color.WHITE);
        mMessageView.setOnSkipClickListener(skipView -> skip());

        addView(
            mMessageView,
            new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );
    }

    private void startAnimationSize() {
        if (!isPerformedAnimationSize) {
            final ValueAnimator circleSizeAnimator = ValueAnimator.ofFloat(
                0f,
                circleIndicatorSizeFinal
            );
            circleSizeAnimator.addUpdateListener(valueAnimator -> {
                circleIndicatorSize = (float) circleSizeAnimator.getAnimatedValue();
                float animatedFraction = circleSizeAnimator.getAnimatedFraction();
                circleInnerIndicatorSize = circleInnerIndicatorSizeFinal * animatedFraction;
                postInvalidate();
            });

            final ValueAnimator linePositionAnimator = ValueAnimator.ofFloat(
                stopY,
                startYLineAndCircle
            );
            linePositionAnimator.addUpdateListener(valueAnimator -> {
                startYLineAndCircle = (float) linePositionAnimator.getAnimatedValue();
                postInvalidate();
            });

            linePositionAnimator.setDuration(SIZE_ANIMATION_DURATION);
            linePositionAnimator.start();
            linePositionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    circleSizeAnimator.setDuration(SIZE_ANIMATION_DURATION);
                    circleSizeAnimator.start();
                    isPerformedAnimationSize = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    private void init() {
        lineIndicatorWidthSize = LINE_INDICATOR_WIDTH_SIZE * density;
        indicatorMargin = MARGIN_INDICATOR * density;
        indicatorHeight = INDICATOR_HEIGHT * density;
        messageViewPadding = (int) (MESSAGE_VIEW_PADDING * density);
        strokeCircleWidth = STROKE_CIRCLE_INDICATOR_SIZE * density;
        circleIndicatorSizeFinal = CIRCLE_INDICATOR_SIZE * density;
        circleInnerIndicatorSizeFinal = circleIndicatorSizeFinal - density;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        post(() -> updateGuideLayout(true));
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getViewTreeObserver().isAlive()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
            } else {
                getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
            }
        }
        super.onDetachedFromWindow();
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        updateGuideLayout(false);
        return super.onApplyWindowInsets(insets);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (target != null) {

            selfPaint.setColor(overlayColor);
            selfPaint.setStyle(Paint.Style.FILL);
            selfPaint.setAntiAlias(true);
            canvas.drawRect(backgroundRect, selfPaint);

            paintLine.setStyle(Paint.Style.FILL);
            paintLine.setColor(lineIndicatorColor);
            paintLine.setStrokeWidth(lineIndicatorWidthSize);
            paintLine.setAntiAlias(true);

            paintCircle.setStyle(Paint.Style.STROKE);
            paintCircle.setColor(circleIndicatorColor);
            paintCircle.setStrokeCap(Paint.Cap.ROUND);
            paintCircle.setStrokeWidth(strokeCircleWidth);
            paintCircle.setAntiAlias(true);

            paintCircleInner.setStyle(Paint.Style.FILL);
            paintCircleInner.setColor(circleInnerIndicatorColor);
            paintCircleInner.setAntiAlias(true);

            final float x = (targetRect.left / 2 + targetRect.right / 2);

            switch (pointerType) {
                case circle:
                    canvas.drawLine(x, startYLineAndCircle, x, stopY, paintLine);
                    canvas.drawCircle(x, startYLineAndCircle, circleIndicatorSize, paintCircle);
                    canvas.drawCircle(
                        x,
                        startYLineAndCircle,
                        circleInnerIndicatorSize,
                        paintCircleInner
                    );
                    break;
                case arrow:
                    canvas.drawLine(x, startYLineAndCircle, x, stopY, paintLine);
                    arrowPath.reset();
                    if (isTop) {
                        arrowPath.moveTo(x, startYLineAndCircle - (circleIndicatorSize * 2));
                    } else {
                        arrowPath.moveTo(x, startYLineAndCircle + (circleIndicatorSize * 2));
                    }
                    arrowPath.lineTo(x + circleIndicatorSize, startYLineAndCircle);
                    arrowPath.lineTo(x - circleIndicatorSize, startYLineAndCircle);
                    arrowPath.close();
                    canvas.drawPath(arrowPath, paintCircle);
                    break;
                case none:
                    //draw no line and no pointer
                    break;
            }
            targetPaint.setXfermode(X_FER_MODE_CLEAR);
            targetPaint.setAntiAlias(true);

            if (target instanceof Targetable) {
                Path guidePath = new Path(((Targetable) target).guidePath());
                guidePath.offset(-guideLocation[0], -guideLocation[1]);
                canvas.drawPath(guidePath, targetPaint);
            } else {
                canvas.drawRoundRect(
                    targetRect,
                    RADIUS_SIZE_TARGET_RECT,
                    RADIUS_SIZE_TARGET_RECT,
                    targetPaint
                );
            }
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void dismiss() {
        dismiss(false);
    }

    public void skip() {
        dismiss(true);
    }

    private void dismiss(boolean skipped) {
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
        mIsShowing = false;
        if (mGuideListener != null) {
            if (skipped) {
                mGuideListener.onSkip(target);
            } else {
                mGuideListener.onDismiss(target);
            }
        }
        if (skipped && mSkipListener != null) {
            mSkipListener.onSkip(target);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (dismissType) {

                case outside:
                    if (!isViewContains(mMessageView, x, y)) {
                        dismiss();
                    }
                    break;

                case anywhere:
                    dismiss();
                    break;

                case targetView:
                    if (targetRect.contains(x, y)) {
                        target.performClick();
                        dismiss();
                    }
                    break;

                case selfView:
                    if (isViewContains(mMessageView, x, y)) {
                        dismiss();
                    }
                    break;

                case outsideTargetAndMessage:
                    if (!(targetRect.contains(x, y) || isViewContains(mMessageView, x, y))) {
                        dismiss();
                    }
            }
            return true;
        }
        return false;
    }

    private boolean isViewContains(View view, float rx, float ry) {
        RectF viewBounds = getViewBoundsOnGuide(view);
        int x = (int) viewBounds.left;
        int y = (int) viewBounds.top;
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }

    private void setMessageLocation(Point p) {
        mMessageView.setX(p.x);
        mMessageView.setY(p.y);
        postInvalidate();
    }

    public void updateGuideViewLocation() {
        updateGuideLayout(false);
    }

    private Point resolveMessageViewLocation() {
        int xMessageView;
        if (mGravity == Gravity.center) {
            xMessageView = Math.round(targetRect.centerX() - (mMessageView.getWidth() / 2f));
        } else {
            xMessageView = Math.round(targetRect.right) - mMessageView.getWidth();
        }

        int minX = safeAreaRect.left;
        int maxX = Math.max(minX, safeAreaRect.right - mMessageView.getWidth());
        xMessageView = clamp(xMessageView, minX, maxX);

        int belowY = Math.round(targetRect.bottom + indicatorHeight);
        int aboveY = Math.round(targetRect.top - mMessageView.getHeight() - indicatorHeight);
        boolean canFitBelow = belowY + mMessageView.getHeight() <= safeAreaRect.bottom;
        boolean canFitAbove = aboveY >= safeAreaRect.top;
        float availableAbove = targetRect.top - safeAreaRect.top;
        float availableBelow = safeAreaRect.bottom - targetRect.bottom;

        if (canFitBelow && (!canFitAbove || availableBelow >= availableAbove)) {
            isTop = true;
            yMessageView = belowY;
        } else if (canFitAbove) {
            isTop = false;
            yMessageView = aboveY;
        } else if (availableBelow >= availableAbove) {
            isTop = true;
            yMessageView = safeAreaRect.bottom - mMessageView.getHeight();
        } else {
            isTop = false;
            yMessageView = safeAreaRect.top;
        }

        int minY = safeAreaRect.top;
        int maxY = Math.max(minY, safeAreaRect.bottom - mMessageView.getHeight());
        yMessageView = clamp(yMessageView, minY, maxY);

        return new Point(xMessageView, yMessageView);
    }

    public void show() {
        this.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.setClickable(false);
        mMessageView.setSkipButtonVisible(showSkipButton);
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(APPEARING_ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);
        mIsShowing = true;
    }

    private void updateGuideLayout(boolean forceStartAnimation) {
        if (target == null || getWidth() == 0 || getHeight() == 0 || target.getWidth() == 0 || target.getHeight() == 0) {
            return;
        }

        targetRect = getViewBoundsOnGuide(target);
        backgroundRect.set(0, 0, getWidth(), getHeight());
        safeAreaRect.set(backgroundRect);
        applyWindowInsetsToSafeArea(safeAreaRect);

        Point messageLocation = resolveMessageViewLocation();
        float signedIndicatorMargin = isTop ? indicatorMargin : -indicatorMargin;
        setMessageLocation(messageLocation);
        startYLineAndCircle = (isTop ? targetRect.bottom : targetRect.top) + signedIndicatorMargin;
        stopY = yMessageView + indicatorHeight + (isTop ? -signedIndicatorMargin : signedIndicatorMargin);

        if (forceStartAnimation) {
            isPerformedAnimationSize = false;
        }

        if (!isPerformedAnimationSize) {
            startAnimationSize();
        } else {
            circleIndicatorSize = circleIndicatorSizeFinal;
            circleInnerIndicatorSize = circleInnerIndicatorSizeFinal;
            invalidate();
        }
    }

    private RectF getViewBoundsOnGuide(View view) {
        getLocationOnScreen(guideLocation);
        if (view instanceof Targetable) {
            RectF bounds = new RectF(((Targetable) view).boundingRect());
            bounds.offset(-guideLocation[0], -guideLocation[1]);
            return bounds;
        }

        view.getLocationOnScreen(targetLocation);
        return new RectF(
            targetLocation[0] - guideLocation[0],
            targetLocation[1] - guideLocation[1],
            targetLocation[0] - guideLocation[0] + view.getWidth(),
            targetLocation[1] - guideLocation[1] + view.getHeight()
        );
    }

    private void applyWindowInsetsToSafeArea(Rect bounds) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        WindowInsets rootInsets = getRootWindowInsets();
        if (rootInsets == null) {
            return;
        }

        int insetLeft;
        int insetTop;
        int insetRight;
        int insetBottom;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets insetsIgnoringVisibility = rootInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
            );
            insetLeft = insetsIgnoringVisibility.left;
            insetTop = insetsIgnoringVisibility.top;
            insetRight = insetsIgnoringVisibility.right;
            insetBottom = insetsIgnoringVisibility.bottom;
        } else {
            insetLeft = rootInsets.getSystemWindowInsetLeft();
            insetTop = rootInsets.getSystemWindowInsetTop();
            insetRight = rootInsets.getSystemWindowInsetRight();
            insetBottom = rootInsets.getSystemWindowInsetBottom();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && rootInsets.getDisplayCutout() != null) {
                insetLeft = Math.max(insetLeft, rootInsets.getDisplayCutout().getSafeInsetLeft());
                insetTop = Math.max(insetTop, rootInsets.getDisplayCutout().getSafeInsetTop());
                insetRight = Math.max(insetRight, rootInsets.getDisplayCutout().getSafeInsetRight());
                insetBottom = Math.max(insetBottom, rootInsets.getDisplayCutout().getSafeInsetBottom());
            }
        }

        bounds.left += insetLeft;
        bounds.top += insetTop;
        bounds.right -= insetRight;
        bounds.bottom -= insetBottom;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public void setTitle(String str) {
        mMessageView.setTitle(str);
    }

    public void setContentText(String str) {
        mMessageView.setContentText(str);
    }

    public void setContentSpan(Spannable span) {
        mMessageView.setContentSpan(span);
    }

    public void setTitleTypeFace(Typeface typeFace) {
        mMessageView.setTitleTypeFace(typeFace);
    }

    public void setContentTypeFace(Typeface typeFace) {
        mMessageView.setContentTypeFace(typeFace);
    }

    public void setTypeFace(Typeface typeFace) {
        mMessageView.setTypeFace(typeFace);
    }

    public void setTitleTextSize(int size) {
        mMessageView.setTitleTextSize(size);
    }

    public void setContentTextSize(int size) {
        mMessageView.setContentTextSize(size);
    }

    public void setTitleTextColor(int color) {
        mMessageView.setTitleTextColor(color);
    }

    public void setContentTextColor(int color) {
        mMessageView.setContentTextColor(color);
    }

    public void setSkipButtonTextColor(int color) {
        mMessageView.setSkipButtonTextColor(color);
    }

    public void setTitleTextStyle(int style) {
        mMessageView.setTitleTextStyle(style);
    }

    public void setContentTextStyle(int style) {
        mMessageView.setContentTextStyle(style);
    }

    public void setSkipButtonText(CharSequence text) {
        mMessageView.setSkipButtonText(text);
    }

    public void setMessageBackgroundColor(int color) {
        mMessageView.setColor(color);
    }

    public void setMessageBackgroundDrawable(Drawable drawable) {
        mMessageView.setCustomBackgroundDrawable(drawable);
    }

    public void setSkipButtonBackgroundDrawable(Drawable drawable) {
        mMessageView.setSkipButtonBackgroundDrawable(drawable);
    }

    public static class Builder {

        private View targetView;
        private String title, contentText;
        private Gravity gravity;
        private DismissType dismissType;
        private PointerType pointerType;
        private final Context context;
        private Spannable contentSpan;
        private Typeface typeFace;
        private Typeface titleTypeFace, contentTypeFace;
        private GuideListener guideListener;
        private SkipListener skipListener;
        private int titleTextSize;
        private int contentTextSize;
        private float lineIndicatorHeight;
        private float lineIndicatorWidthSize;
        private float circleIndicatorSize;
        private float circleInnerIndicatorSize;
        private float strokeCircleWidth;
        private boolean showSkipButton;
        private Integer titleTextColor;
        private Integer contentTextColor;
        private Integer skipButtonTextColor;
        private Integer titleTextStyle;
        private Integer contentTextStyle;
        private Integer messageBackgroundColor;
        private Integer overlayColor;
        private Integer lineIndicatorColor;
        private Integer circleIndicatorColor;
        private Integer circleInnerIndicatorColor;
        private CharSequence skipButtonText;
        private Drawable messageBackgroundDrawable;
        private Drawable skipButtonBackgroundDrawable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTargetView(View view) {
            this.targetView = view;
            return this;
        }

        /**
         * gravity GuideView
         *
         * @param gravity it should be one type of Gravity enum.
         **/
        public Builder setGravity(Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * defining a title
         *
         * @param title a title. for example: submit button.
         **/
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * defining a description for the target view
         *
         * @param contentText a description. for example: this button can for submit your information..
         **/
        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        /**
         * setting spannable type
         *
         * @param span a instance of spannable
         **/
        public Builder setContentSpan(Spannable span) {
            this.contentSpan = span;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/
        public Builder setContentTypeFace(Typeface typeFace) {
            this.contentTypeFace = typeFace;
            return this;
        }

        /**
         * setting font family for both title and content
         *
         * @param typeFace a shared typeface instance
         **/
        public Builder setTypeFace(Typeface typeFace) {
            this.typeFace = typeFace;
            return this;
        }

        /**
         * adding a listener on show case view
         *
         * @param guideListener a listener for events
         **/
        public Builder setGuideListener(GuideListener guideListener) {
            this.guideListener = guideListener;
            return this;
        }

        /**
         * adding a listener when the explicit skip action is pressed
         *
         * @param skipListener a listener for skip events
         **/
        public Builder setSkipListener(SkipListener skipListener) {
            this.skipListener = skipListener;
            return this;
        }

        /**
         * setting font type face
         *
         * @param typeFace a instance of type face (font family)
         **/
        public Builder setTitleTypeFace(Typeface typeFace) {
            this.titleTypeFace = typeFace;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setContentTextSize(int size) {
            this.contentTextSize = size;
            return this;
        }

        /**
         * the defined text size overrides any defined size in the default or provided style
         *
         * @param size title text by sp unit
         * @return builder
         */
        public Builder setTitleTextSize(int size) {
            this.titleTextSize = size;
            return this;
        }

        /**
         * applying a color to the title text
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setTitleTextColor(int color) {
            this.titleTextColor = color;
            return this;
        }

        /**
         * applying a color to the description text
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setContentTextColor(int color) {
            this.contentTextColor = color;
            return this;
        }

        /**
         * applying a color to both title and description text
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setTextColor(int color) {
            this.titleTextColor = color;
            this.contentTextColor = color;
            return this;
        }

        /**
         * applying a style such as Typeface.BOLD to the title text
         *
         * @param style Typeface style constant
         * @return builder
         */
        public Builder setTitleTextStyle(int style) {
            this.titleTextStyle = style;
            return this;
        }

        /**
         * applying a style such as Typeface.BOLD to the description text
         *
         * @param style Typeface style constant
         * @return builder
         */
        public Builder setContentTextStyle(int style) {
            this.contentTextStyle = style;
            return this;
        }

        /**
         * applying the same style to title and description text
         *
         * @param style Typeface style constant
         * @return builder
         */
        public Builder setTextStyle(int style) {
            this.titleTextStyle = style;
            this.contentTextStyle = style;
            return this;
        }

        /**
         * this method defining the type of dismissing function
         *
         * @param dismissType should be one type of DismissType enum. for example: outside -> Dismissing with click on outside of MessageView
         */
        public Builder setDismissType(DismissType dismissType) {
            this.dismissType = dismissType;
            return this;
        }

        /**
         * changing line height indicator
         *
         * @param height you can change height indicator (Converting to Dp)
         */
        public Builder setIndicatorHeight(float height) {
            this.lineIndicatorHeight = height;
            return this;
        }

        /**
         * changing line width indicator
         *
         * @param width you can change width indicator
         */
        public Builder setIndicatorWidthSize(float width) {
            this.lineIndicatorWidthSize = width;
            return this;
        }

        /**
         * changing circle size indicator
         *
         * @param size you can change circle size indicator
         */
        public Builder setCircleIndicatorSize(float size) {
            this.circleIndicatorSize = size;
            return this;
        }

        /**
         * changing inner circle size indicator
         *
         * @param size you can change inner circle indicator size
         */
        public Builder setCircleInnerIndicatorSize(float size) {
            this.circleInnerIndicatorSize = size;
            return this;
        }

        /**
         * changing stroke circle size indicator
         *
         * @param size you can change stroke circle indicator size
         */
        public Builder setCircleStrokeIndicatorSize(float size) {
            this.strokeCircleWidth = size;
            return this;
        }

        /**
         * customizing the message card background color
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setMessageBackgroundColor(int color) {
            this.messageBackgroundColor = color;
            return this;
        }

        /**
         * customizing the message card background drawable
         *
         * @param drawable card drawable or selector
         * @return builder
         */
        public Builder setMessageBackgroundDrawable(Drawable drawable) {
            this.messageBackgroundDrawable = drawable;
            return this;
        }

        /**
         * customizing the overlay color behind the highlighted target
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setOverlayColor(int color) {
            this.overlayColor = color;
            return this;
        }

        /**
         * customizing the pointer line color
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setLineIndicatorColor(int color) {
            this.lineIndicatorColor = color;
            return this;
        }

        /**
         * customizing the outer pointer color
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setCircleIndicatorColor(int color) {
            this.circleIndicatorColor = color;
            return this;
        }

        /**
         * customizing the inner pointer color
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setCircleInnerIndicatorColor(int color) {
            this.circleInnerIndicatorColor = color;
            return this;
        }

        /**
         * this method defining the type of pointer
         *
         * @param pointerType should be one type of PointerType enum. for example: arrow -> To show arrow pointing to target view
         */
        public Builder setPointerType(PointerType pointerType) {
            this.pointerType = pointerType;
            return this;
        }

        /**
         * showing an explicit skip affordance for guide sequences
         *
         * @param showSkipButton true to show a close button in the message card
         */
        public Builder setShowSkipButton(boolean showSkipButton) {
            this.showSkipButton = showSkipButton;
            return this;
        }

        /**
         * replacing the default skip button label
         *
         * @param text text shown in the skip button
         * @return builder
         */
        public Builder setSkipButtonText(CharSequence text) {
            this.skipButtonText = text;
            return this;
        }

        /**
         * customizing the skip button text color
         *
         * @param color Android color int
         * @return builder
         */
        public Builder setSkipButtonTextColor(int color) {
            this.skipButtonTextColor = color;
            return this;
        }

        /**
         * customizing the skip button background drawable or selector
         *
         * @param drawable button drawable or selector
         * @return builder
         */
        public Builder setSkipButtonBackgroundDrawable(Drawable drawable) {
            this.skipButtonBackgroundDrawable = drawable;
            return this;
        }

        public GuideView build() {
            GuideView guideView = new GuideView(context, targetView);
            guideView.mGravity = gravity != null ? gravity : Gravity.auto;
            guideView.dismissType = dismissType != null ? dismissType : DismissType.targetView;
            guideView.pointerType = pointerType != null ? pointerType : PointerType.circle;
            guideView.showSkipButton = showSkipButton;
            float density = context.getResources().getDisplayMetrics().density;

            guideView.setTitle(title);
            if (contentText != null) {
                guideView.setContentText(contentText);
            }
            if (titleTextSize != 0) {
                guideView.setTitleTextSize(titleTextSize);
            }
            if (contentTextSize != 0) {
                guideView.setContentTextSize(contentTextSize);
            }
            if (contentSpan != null) {
                guideView.setContentSpan(contentSpan);
            }
            if (typeFace != null) {
                guideView.setTypeFace(typeFace);
            }
            if (titleTypeFace != null) {
                guideView.setTitleTypeFace(titleTypeFace);
            }
            if (contentTypeFace != null) {
                guideView.setContentTypeFace(contentTypeFace);
            }
            if (titleTextColor != null) {
                guideView.setTitleTextColor(titleTextColor);
            }
            if (contentTextColor != null) {
                guideView.setContentTextColor(contentTextColor);
            }
            if (skipButtonTextColor != null) {
                guideView.setSkipButtonTextColor(skipButtonTextColor);
            }
            if (titleTextStyle != null) {
                guideView.setTitleTextStyle(titleTextStyle);
            }
            if (contentTextStyle != null) {
                guideView.setContentTextStyle(contentTextStyle);
            }
            if (skipButtonText != null) {
                guideView.setSkipButtonText(skipButtonText);
            }
            if (messageBackgroundColor != null) {
                guideView.setMessageBackgroundColor(messageBackgroundColor);
            }
            if (messageBackgroundDrawable != null) {
                guideView.setMessageBackgroundDrawable(messageBackgroundDrawable);
            }
            if (skipButtonBackgroundDrawable != null) {
                guideView.setSkipButtonBackgroundDrawable(skipButtonBackgroundDrawable);
            }
            if (guideListener != null) {
                guideView.mGuideListener = guideListener;
            }
            if (skipListener != null) {
                guideView.mSkipListener = skipListener;
            }
            if (lineIndicatorHeight != 0) {
                guideView.indicatorHeight = lineIndicatorHeight * density;
            }
            if (lineIndicatorWidthSize != 0) {
                guideView.lineIndicatorWidthSize = lineIndicatorWidthSize * density;
            }
            if (circleIndicatorSize != 0) {
                guideView.circleIndicatorSizeFinal = circleIndicatorSize * density;
            }
            if (circleInnerIndicatorSize != 0) {
                guideView.circleInnerIndicatorSizeFinal = circleInnerIndicatorSize * density;
            }
            if (strokeCircleWidth != 0) {
                guideView.strokeCircleWidth = strokeCircleWidth * density;
            }
            if (overlayColor != null) {
                guideView.overlayColor = overlayColor;
            }
            if (lineIndicatorColor != null) {
                guideView.lineIndicatorColor = lineIndicatorColor;
            }
            if (circleIndicatorColor != null) {
                guideView.circleIndicatorColor = circleIndicatorColor;
            }
            if (circleInnerIndicatorColor != null) {
                guideView.circleInnerIndicatorColor = circleInnerIndicatorColor;
            }

            return guideView;
        }
    }
}
