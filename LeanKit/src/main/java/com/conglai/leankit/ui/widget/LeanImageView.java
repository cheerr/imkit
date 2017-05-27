package com.conglai.leankit.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conglai.common.Debug;
import com.conglai.leankit.ui.widget.util.ShadowDelegate;

/**
 * Created by chenwei on 16/7/22.
 */

public class LeanImageView extends ImageView {

    private static final String TAG = LeanImageView.class.getSimpleName();

    private static final PorterDuffXfermode PORTER_DUFF_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

    private Canvas maskCanvas;
    private Bitmap maskBitmap;
    private Paint maskPaint;

    private Canvas drawableCanvas;
    private Bitmap drawableBitmap;
    private Paint drawablePaint;

    private boolean invalidated = true;
    private boolean square = false;

    private Drawable shape;
    private Matrix matrix;
    private Matrix drawMatrix;

    ShadowDelegate delegate;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (delegate != null)
            delegate.delegateTouch(ev);
        return super.onTouchEvent(ev);
    }

    public void setClickShadow(boolean clickShadow) {
        if (delegate == null) {
            delegate = new ShadowDelegate(this);
        }
        delegate.setClickShadow(clickShadow);
    }

    public LeanImageView(Context context) {
        this(context, null);
    }

    public LeanImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    @SuppressWarnings("SameParameterValue")
    public void setSquare(boolean square) {
        this.square = square;
    }


    public void setShape(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shape = getResources().getDrawable(resId, null);
        } else {
            shape = getResources().getDrawable(resId);
        }
        maskCanvas = null;
        createMaskCanvas(getMeasuredWidth(), getMeasuredHeight(), 0, 0);
        invalidate();
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, com.github.siyamed.shapeimageview.R.styleable.ShaderImageView, defStyle, 0);
            square = typedArray.getBoolean(com.github.siyamed.shapeimageview.R.styleable.ShaderImageView_siSquare, false);
            shape = typedArray.getDrawable(com.github.siyamed.shapeimageview.R.styleable.ShaderImageView_siShape);
            typedArray.recycle();
        }

        if (getScaleType() == ScaleType.FIT_CENTER) {
            setScaleType(ScaleType.CENTER_CROP);
        }

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColor(Color.BLACK);
        matrix = new Matrix();
    }

    public void invalidate() {
        invalidated = true;
        super.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMaskCanvas(w, h, oldw, oldh);

        Debug.i("LeanImageView", "onSizeChanged w=" + w + ";oldw=" + oldw);
    }

    private void createMaskCanvas(int width, int height, int oldw, int oldh) {
        boolean sizeChanged = width != oldw || height != oldh;
        boolean isValid = width > 0 && height > 0;
        if (isValid && (maskCanvas == null || sizeChanged)) {
            maskCanvas = new Canvas();
            maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            maskCanvas.setBitmap(maskBitmap);

            maskPaint.reset();
            paintMaskCanvas(maskCanvas, maskPaint, width, height);

            drawableCanvas = new Canvas();
            drawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            drawableCanvas.setBitmap(drawableBitmap);
            drawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            invalidated = true;
        }
    }

    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        if (shape != null) {
            if (shape instanceof BitmapDrawable) {
                configureBitmapBounds(width, height);
                if (drawMatrix != null) {
                    int drawableSaveCount = maskCanvas.getSaveCount();
                    maskCanvas.save();
                    maskCanvas.concat(matrix);
                    shape.draw(maskCanvas);
                    maskCanvas.restoreToCount(drawableSaveCount);
                    return;
                }
            }

            shape.setBounds(0, 0, width, height);
            shape.draw(maskCanvas);
        }
    }

    private void configureBitmapBounds(int viewWidth, int viewHeight) {
        drawMatrix = null;
        int drawableWidth = shape.getIntrinsicWidth();
        int drawableHeight = shape.getIntrinsicHeight();
        boolean fits = viewWidth == drawableWidth && viewHeight == drawableHeight;

        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            shape.setBounds(0, 0, drawableWidth, drawableHeight);
            float widthRatio = (float) viewWidth / (float) drawableWidth;
            float heightRatio = (float) viewHeight / (float) drawableHeight;
            float scale = Math.min(widthRatio, heightRatio);
            float dx = (int) ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f);
            float dy = (int) ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f);

            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            int saveCount = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            try {
                if (invalidated) {
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        invalidated = false;
                        Matrix imageMatrix = getImageMatrix();
                        if (imageMatrix == null) {// && mPaddingTop == 0 && mPaddingLeft == 0) {
                            drawable.draw(drawableCanvas);
                        } else {
                            int drawableSaveCount = drawableCanvas.getSaveCount();
                            drawableCanvas.save();
                            drawableCanvas.concat(imageMatrix);
                            drawable.draw(drawableCanvas);
                            drawableCanvas.restoreToCount(drawableSaveCount);
                        }

                        drawablePaint.reset();
                        drawablePaint.setFilterBitmap(false);
                        drawablePaint.setXfermode(PORTER_DUFF_XFERMODE);
                        drawableCanvas.drawBitmap(maskBitmap, 0.0f, 0.0f, drawablePaint);
                    }
                }

                if (!invalidated) {
                    drawablePaint.setXfermode(null);
                    canvas.drawBitmap(drawableBitmap, 0.0f, 0.0f, drawablePaint);
                }
            } catch (Exception e) {
                String log = "Exception occured while drawing " + getId();
                Log.e(TAG, log, e);
            } finally {
                canvas.restoreToCount(saveCount);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (square) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int dimen = Math.min(width, height);
            setMeasuredDimension(dimen, dimen);
        }
    }

    /**
     * 重置长宽
     *
     * @param width
     * @param height
     */
    public void resetWidthAndHeight(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = height;
        params.width = width;
        setLayoutParams(params);
    }
}
