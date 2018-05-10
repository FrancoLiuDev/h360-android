package com.leedian.klozr.utils.viewUtils.tools;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;

/**
 * ScanMaskView
 *
 * @author Franco
 */
public class ScanMaskView
        extends LinearLayout
{
    private static final int mask_percent_to_width = 58;
    private int focusInnerAreaCornerLength;
    private int   color_mask               = AppResource.getColor(R.color.color_scan_view_mask);
    private int   color_reverse_scan_area  = AppResource.getColor(R.color.color_transparent);
    private int   color_focus_boarder      = AppResource
            .getColor(R.color.color_scan_view_focus_boarder);
    private int   color_tips_text          = AppResource.getColor(R.color.color_white);
    private float tip_text_size_second_lag = AppResource
            .Dimens(R.dimen.app_font_content_size_small);
    private float tip_text_size            = AppResource.Dimens(R.dimen.app_font_tip_size);
    private Bitmap bitmap;
    private Canvas canvas;
    private Rect   mask_rect;
    private Paint  textPaint;
    private Paint  eraser;
    private Paint  accent_paint;

    public ScanMaskView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    private void init() {

        setWillNotDraw(false);

        eraser = new Paint();
        eraser.setColor(color_reverse_scan_area);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        accent_paint = new Paint();
        accent_paint.setColor(color_focus_boarder);
        accent_paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        accent_paint.setStrokeWidth(5);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(color_tips_text);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(tip_text_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int width                = this.getWidth();
        int height               = this.getHeight();
        int focusOuterAreaLength = width * mask_percent_to_width / 100;

        int focusInnerAreaSideShiftFromOuterSide = focusOuterAreaLength / 50;
        focusInnerAreaCornerLength = focusOuterAreaLength / 8;

        int PosX = (width - focusOuterAreaLength) / 2;
        int PosY = (height - focusOuterAreaLength) / 2;
        mask_rect = new Rect(PosX, PosY, PosX + focusOuterAreaLength, PosY + focusOuterAreaLength);

        if (bitmap == null || canvas == null) {
            if (bitmap != null) bitmap.recycle();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }

        this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.canvas.drawColor(color_mask);
        this.canvas.drawRect(mask_rect, eraser);
        Rect focusArea = getMaskInnerRect(mask_rect, focusInnerAreaSideShiftFromOuterSide);
        drawFocusArea(focusArea);
        drawTipArea();

        assert canvas != null;

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    private Rect getMaskInnerRect(Rect outRect, int sideShift) {

        int left   = outRect.left + sideShift;
        int top    = outRect.top + sideShift;
        int right  = outRect.right - sideShift;
        int bottom = outRect.bottom - sideShift;
        return new Rect(left, top, right, bottom);
    }

    private void drawFocusArea(Rect outRect) {

        int lengthVal = focusInnerAreaCornerLength;
        this.canvas
                .drawLine(outRect.left, outRect.top, outRect.left + lengthVal, outRect.top, accent_paint);
        this.canvas
                .drawLine(outRect.left, outRect.top, outRect.left, outRect.top + lengthVal, accent_paint);
        this.canvas
                .drawLine(outRect.right, outRect.top, outRect.right - lengthVal, outRect.top, accent_paint);
        this.canvas
                .drawLine(outRect.right, outRect.top, outRect.right, outRect.top + lengthVal, accent_paint);
        this.canvas
                .drawLine(outRect.left, outRect.bottom, outRect.left + lengthVal, outRect.bottom, accent_paint);
        this.canvas
                .drawLine(outRect.left, outRect.bottom, outRect.left, outRect.bottom - lengthVal, accent_paint);
        this.canvas
                .drawLine(outRect.right, outRect.bottom, outRect.right - lengthVal, outRect.bottom, accent_paint);
        this.canvas
                .drawLine(outRect.right, outRect.bottom, outRect.right, outRect.bottom - lengthVal, accent_paint);
    }

    private void drawTipArea() {

        int point_index;
        int point_x                  = mask_rect.left + (mask_rect.width() / 2);
        int tips_padding_below_focus = 30;
        String str = AppResource
                .getString(R.string.please_place_qrcode_inorder_to_scan);
        String str2 = AppResource
                .getString(R.string.please_place_qrcode_inorder_to_scan_sec_lag);
        Rect rect = new Rect();

        textPaint.getTextBounds(str, 0, str.length(), rect);

        point_index = mask_rect.bottom + rect.height() + tips_padding_below_focus;
        this.canvas.drawText(str, point_x, point_index, textPaint);

        textPaint.setTextSize(tip_text_size_second_lag);

        textPaint.getTextBounds(str2, 0, str2.length(), rect);
        point_index = point_index + rect.height() + tips_padding_below_focus;
        this.canvas.drawText(str2, point_x, point_index, textPaint);
    }
}
