package com.leedian.klozr.utils.viewUtils.tools;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.utils.viewUtils.MatrixPosition;
import com.leedian.klozr.utils.viewUtils.Utils;

/**
 * InfoTextDisplayTool
 *
 * @author Franco
 */
public class InfoTextDisplayTool {
    private static final int   sideLeft    = 0;
    private static final int   sideTop     = 1;
    private static final int   sideRight   = 2;
    private static final int   sideBottom  = 3;
    private              float distLeft    = 0;
    private              float distTop     = 0;
    private              float distRight   = 0;
    private              float distBottom  = 0;
    private              RectF displayRect = new RectF();
    private MatrixPosition displayMatrixPosition;
    private RectF          infoNodeRect;
    private TextView       infoEditorTextView;
    private RectF bmp_display_rect = new RectF();
    private Paint textPaint;

    public InfoTextDisplayTool(MatrixPosition matrixPosition, RectF InfoNodeRect, TextView infoEditorTextView) {

        this.displayMatrixPosition = matrixPosition;
        this.infoNodeRect = InfoNodeRect;
        this.infoEditorTextView = infoEditorTextView;
        init();
    }

    private void init() {

        bmp_display_rect = displayMatrixPosition.getMatrixDisplayRectF();
        distLeft = infoNodeRect.left - bmp_display_rect.left;
        distTop = infoNodeRect.top - bmp_display_rect.top;
        distRight = bmp_display_rect.right - infoNodeRect.right;
        distBottom = bmp_display_rect.bottom - infoNodeRect.bottom;
        Paint Paint = infoEditorTextView.getPaint();
        textPaint = new Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Paint.getColor());
        textPaint.setTypeface(Paint.getTypeface());
        textPaint.setTextAlign(Paint.getTextAlign());
        textPaint.setTextSize((float) (Paint.getTextSize() + (Paint.getTextSize() * 0.1)));
        calculate();
    }

    private Size getCalculateTextRect(int width) {

        String str = infoEditorTextView.getText().toString();

        if (str.length() == 0) { str = AppResource.getString(R.string.please_edit_infonode); }

        return getTextViewDisplaySize(infoEditorTextView, str, width, 4);
    }

    private Size getCalculateTextDisplaySize(boolean isSide) {

        int sideMax;
        if (isSide) { sideMax = getDisplayMaxWidthSide(); } else { sideMax = getDisplayMaxWidth(); }
        return getCalculateTextRect(sideMax);
    }

    private Size getTextViewDisplaySize(TextView textView, CharSequence text, int deviceWidth, int padding) {

        int widthMeasureSpec;
        int heightMeasureSpec;
        int height;

        Size size;

        textView.setPadding(0, 0, 0, 0);
        textView.setTypeface(textView.getPaint().getTypeface());
        textView.setText(text, TextView.BufferType.SPANNABLE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getPaint().getTextSize());

        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        textView.measure(widthMeasureSpec, heightMeasureSpec);
        Size sizeY = new Size(textView.getMeasuredWidth(), textView.getMeasuredHeight());

        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(sizeY.getHeight(),
                                                             View.MeasureSpec.EXACTLY);

        textView.measure(widthMeasureSpec, heightMeasureSpec);
        Size sizeX = new Size(textView.getMeasuredWidth(), textView.getMeasuredHeight());

        height = sizeY.getHeight();

        if (height < getDisplayMinHeight()) { height = getDisplayMinHeight(); }

        if (sizeX.getWidth() > sizeY.getWidth()) {
            size = new Size(sizeY.getWidth(), height);
        } else { size = new Size(sizeX.getWidth(), height); }

        return size;
    }

    private int getDisplayMaxWidth() {

        return (int) (displayMatrixPosition.getImageViewWidth() * 0.5);
    }

    private int getDisplayMaxWidthSide() {

        return (int) (displayMatrixPosition.getImageViewWidth() * 0.3);
    }

    private int getDisplayMinWidth() {

        return (int) (displayMatrixPosition.getImageViewWidth() * 0.25);
    }

    private int getDisplayMinHeight() {

        return Utils.dpToPx(40);
    }

    private int determineSideWidth() {

        return (int) (displayMatrixPosition.getImageViewWidth() * 0.45);
    }

    private void calculate() {

        if (getCouldBePlaceSide()) {
            determineMuftiLineSide();
        } else { determineMuftiLineCenter(); }
    }

    private void determineMuftiLineSide() {

        if (getSideSpace(sideLeft) > getSideSpace(sideRight)) { makeFinalSpaceLeft(); } else {
            makeFinalSpaceRight();
        }
    }

    private void determineMuftiLineCenter() {

        if (getSideSpace(sideTop) > getSideSpace(sideBottom)) { makeFinalSpaceTop(); } else {
            makeFinalSpaceBottom();
        }
    }

    public RectF getDisplayRect() {

        return displayRect;
    }

    private void makeFinalSpaceTop() {

        Size displaySize = getCalculateTextDisplaySize(true);
        int  ViewWidth   = displaySize.getWidth() + Utils.dpToPx(36);
        int  ViewHeight  = displaySize.getHeight();
        int  MaxHeight   = (int) getSideSpace(sideTop) - 10;
        if (ViewHeight > MaxHeight) { ViewHeight = MaxHeight; }
        displayRect.left = infoNodeRect.centerX() - (ViewWidth / 2);
        displayRect.right = infoNodeRect.centerX() + (ViewWidth / 2);
        displayRect.bottom = infoNodeRect.top - 10;
        displayRect.top = displayRect.bottom - ViewHeight;
        displayRect.top = displayRect.bottom - ViewHeight;
    }

    private void makeFinalSpaceBottom() {

        Size displaySize = getCalculateTextDisplaySize(true);
        int  ViewWidth   = displaySize.getWidth() + Utils.dpToPx(36);
        displayRect.left = infoNodeRect.centerX() - (ViewWidth / 2);
        displayRect.right = infoNodeRect.centerX() + (ViewWidth / 2);
        displayRect.top = infoNodeRect.bottom + 10;
        displayRect.bottom = displayRect.top + displaySize.getHeight();
        displayRect.bottom = displayRect.top + displaySize.getHeight();
    }

    private void makeFinalSpaceLeft() {

        Size displaySize = getCalculateTextDisplaySize(true);
        int  ViewWidth   = displaySize.getWidth() + Utils.dpToPx(36);
        int  ViewHeight  = displaySize.getHeight();
        displayRect.right = infoNodeRect.left - 10;
        displayRect.left = displayRect.right - ViewWidth;
        displayRect.bottom = infoNodeRect.centerY() + (ViewHeight / 2);
        displayRect.top = displayRect.bottom - ViewHeight;
        displayRect.top = displayRect.bottom - ViewHeight;
    }

    private void makeFinalSpaceRight() {

        Size displaySize = getCalculateTextDisplaySize(true);
        int  ViewWidth   = displaySize.getWidth() + Utils.dpToPx(36);
        int  ViewHeight  = displaySize.getHeight();
        displayRect.left = infoNodeRect.right + 10;
        displayRect.right = displayRect.left + ViewWidth;
        displayRect.bottom = infoNodeRect.centerY() + (ViewHeight / 2);
        displayRect.top = displayRect.bottom - ViewHeight;
        displayRect.top = displayRect.bottom - ViewHeight;
    }

    private int getSpaceBiggestSide() {

        float space[]   = {distLeft, distTop, distRight, distBottom};
        float sideSpace = 0;
        int   index     = 0;
        for (int i = 0; i < space.length; i++) {
            if (space[i] > sideSpace) {
                index = i;
                sideSpace = space[i];
            }
        }
        return index;
    }

    private float getSideSpace(int side) {

        float space[] = {distLeft, distTop, distRight, distBottom};
        return space[side];
    }

    private boolean getCouldBePlaceSide() {

        if (getSideSpace(sideLeft) > determineSideWidth()) { return true; }
        return getSideSpace(sideRight) > determineSideWidth();
    }
}
