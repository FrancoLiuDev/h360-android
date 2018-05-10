package com.leedian.klozr.utils.viewUtils.InfoNode;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.utils.DialogUtil;
import com.leedian.klozr.utils.viewUtils.InfoNode.shape.Circle;
import com.leedian.klozr.utils.viewUtils.InfoNode.target.Target;
import com.leedian.klozr.utils.viewUtils.InfoNode.target.ViewTarget;
import com.leedian.klozr.utils.viewUtils.InfoNode.view.ImagePosView;
import com.leedian.klozr.utils.viewUtils.MatrixPosition;
import com.leedian.klozr.utils.viewUtils.Utils;
import com.leedian.klozr.utils.viewUtils.tools.InfoTextDisplayTool;

/**
 * InfoNodeContentView
 *
 * @author Franco
 */
public class InfoNodeContentView
        extends RelativeLayout
        implements View.OnClickListener
{
    OnTouchListener cardCardViewTouch = new OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {

            return true;
        }
    };

    private RectF bmp_display_rect = null;

    private OnInfoNodeEventListener actionListener;

    private ImagePosView contentImageView;

    private Target targetView;

    private boolean touchRadius;

    private boolean isEditor = false;

    private boolean isAddMode = false;

    private boolean isShowDebugDraw = false;

    private Paint eraser;

    private Paint centerPaint;

    private Paint outerPaint;

    private Paint RectPaint;

    private Bitmap infoMaskBitmap;

    private Canvas infoMaskCanvas;

    private int info_MaskColor;

    private int infoRectColor;

    private int bmp_displayMaskColor;

    private int infoPosX;

    private int infoPosY;

    private int infoRadius;

    private int centerRadius;

    private String infoDescription;

    private ImageView infoNodeCircleView;

    private Bitmap infoNodeCircleBitmap;

    private RectF infoNodeCircleRect = null;

    private Circle infoEditorCircle;

    private View infoEditorView;

    private TextView infoEditorTextView;

    private ImageView infoEditorAddImage;

    public InfoNodeContentView(Context context, AttributeSet attrs) {

        super(context, attrs);
        setWillNotDraw(false);

        View layoutInfo = LayoutInflater.from(getContext())
                                        .inflate(R.layout.view_infonode_text_card, this, false);

        infoEditorView = layoutInfo.findViewById(R.id.info_layout);
        infoEditorView.setVisibility(INVISIBLE);
        ViewGroup Group = (ViewGroup) infoEditorView;

        info_MaskColor = AppResource.getColor(R.color.color_transparent_black_light);
        bmp_displayMaskColor = 0x77FF0000;
        infoRectColor = AppResource.getColor(R.color.color_main_transparent_light);

        CardView card = (CardView) Group.findViewById(R.id.card_layout);
        infoEditorTextView = (TextView) Group.findViewById(R.id.text_view_info);
        infoEditorAddImage = (ImageView) Group.findViewById(R.id.image_view_icon);
        infoEditorAddImage.setOnClickListener(this);

        card.setOnTouchListener(cardCardViewTouch);
        card.setCardBackgroundColor(infoRectColor);
        InfoNodeContentView.this.addView(infoEditorView);

        eraser = new Paint();
        eraser.setColor(0xFFFFFFFF);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        centerPaint = new Paint();
        centerPaint.setColor(0xAAFFFFFF);
        centerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        centerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        outerPaint = new Paint();
        outerPaint.setColor(0xAAFFFFFF);
        outerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        outerPaint.setStyle(Paint.Style.STROKE);

        RectPaint = new Paint();
    }

    public int getInfoPosX() {

        return infoPosX;
    }

    public void setInfoPosX(int infoPosX) {

        this.infoPosX = infoPosX;
    }

    public void setAddMode(boolean addMode) {

        isAddMode = addMode;
    }

    public int getInfoPosY() {

        return infoPosY;
    }

    public void setInfoPosY(int infoPosY) {

        this.infoPosY = infoPosY;
    }

    public int getInfoRadius() {

        return infoRadius;
    }

    public void setInfoRadius(int infoRadius) {

        this.infoRadius = infoRadius;
    }

    public String getInfoDescription() {

        return infoDescription;
    }

    public void setInfoDescription(String infoDescription) {

        this.infoDescription = infoDescription;
    }

    public void setTargetImageView(ImagePosView view) {

        contentImageView = view;
    }

    public void setInfoNodeInfo(String info, float PosX, float PosY, float radius) {

        Size size = contentImageView.getBmpSize();

        Point pos = Utils.percentToPoint(PosX, PosY, size);

        float bmpRadius = Utils.percentToRadiusLength((float) 0.10, size);

        if (pos.x < 0 || pos.x > size.getWidth()) {
            pos.x = size.getWidth() / 2;
        }

        if (pos.y < 0 || pos.y > size.getHeight()) {
            pos.y = size.getHeight() / 2;
        }

        if (bmpRadius <= 0) {
            bmpRadius = 100;
        }

        if ((pos.x + bmpRadius) >= size.getWidth()) {
            bmpRadius = size.getWidth() - pos.x;
        }

        if ((pos.x - bmpRadius) <= 0) {
            bmpRadius = pos.x - 0;
        }

        if ((pos.y + bmpRadius) >= size.getHeight()) {
            bmpRadius = size.getHeight() - pos.y;
        }

        if ((pos.y - bmpRadius) <= 0) {
            bmpRadius = pos.y - 0;
        }

        infoEditorTextView.setText(info);
        infoDescription = infoEditorTextView.getText().toString();
        infoPosX = pos.x;
        infoPosY = pos.y;
        infoRadius = (int) bmpRadius;
    }

    public void setInfoNodeEditor(boolean isEditor) {

        this.isEditor = isEditor;
    }

    @Override protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {

        super.onSizeChanged(w, h, oldWidth, oldHeight);
        updateInfoView();
        this.invalidate();
    }

    protected void updateInfoView() {

        updateInfoCircle();
        updateInfoEditCircle();
    }

    private RectF calculateDescriptionAvailableRect() {

        MatrixPosition      matrixPosition = new MatrixPosition(contentImageView);
        InfoTextDisplayTool displayTool    = new InfoTextDisplayTool(matrixPosition, infoNodeCircleRect, infoEditorTextView);
        return displayTool.getDisplayRect();
    }

    private void updateInfoDescription() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override public void run() {

                final InfoNodeContentView infoContentView = InfoNodeContentView.this;
                infoEditorTextView.setText(infoDescription);
                bmp_display_rect = calculateDescriptionAvailableRect();

                RelativeLayout.LayoutParams infoDialogParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

                ((RelativeLayout) infoEditorView).setGravity(Gravity.NO_GRAVITY);

                int marginsLeft   = (int) bmp_display_rect.left;
                int marginsTop    = (int) bmp_display_rect.top;
                int marginsRight  = (int) (infoContentView.getWidth() - bmp_display_rect.right);
                int marginsBottom = (int) (infoContentView.getHeight() - bmp_display_rect.bottom);

                infoDialogParams.setMargins(marginsLeft, marginsTop, marginsRight, marginsBottom);

                infoEditorView.setLayoutParams(infoDialogParams);
                infoEditorView.postInvalidate();
                infoEditorView.setVisibility(VISIBLE);
            }
        });
    }

    protected void updateInfoCircle() {

        if (infoNodeCircleView == null) {
            return;
        }

        infoNodeCircleView.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });

        MatrixPosition matrixPosition = new MatrixPosition(contentImageView);
        infoNodeCircleView.setScaleX(matrixPosition.getMatrixScaleX());
        infoNodeCircleView.setScaleY(matrixPosition.getMatrixScaleY());

        PointF mPos = matrixPosition.getMatrixPosition(infoPosX, infoPosY);

        infoNodeCircleView.setX(mPos.x - infoRadius);
        infoNodeCircleView.setY(mPos.y - infoRadius);
        infoNodeCircleView.getViewTreeObserver()
                          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                              @Override public void onGlobalLayout() {

                                  PointF mPos;

                                  infoNodeCircleView.getViewTreeObserver()
                                                    .removeOnGlobalLayoutListener(this);

                                  MatrixPosition matrixPosition = new MatrixPosition(contentImageView);
                                  scaleView(infoNodeCircleView, 1f, matrixPosition
                                                                            .getMatrixScaleX() + 0.1f);

                                  mPos = matrixPosition.getMatrixPosition(infoPosX, infoPosY);

                                  float circleWidth = infoNodeCircleView
                                                              .getWidth() * (matrixPosition
                                                                                     .getMatrixScaleX() + 0.1f);
                                  float circleHeight = infoNodeCircleView
                                                               .getWidth() * (matrixPosition
                                                                                      .getMatrixScaleX() + 0.1f);

                                  float circleLeft   = mPos.x - circleWidth / 2;
                                  float circleRight  = mPos.x + circleWidth / 2;
                                  float circleTop    = mPos.y - circleHeight / 2;
                                  float circleBottom = mPos.y + circleHeight / 2;

                                  infoNodeCircleRect = new RectF(circleLeft, circleTop, circleRight, circleBottom);
                                  updateInfoDescription();
                                  InfoNodeContentView.this.invalidate();
                              }
                          });
    }

    @Override protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        RectPaint.setColor(infoRectColor);

        if (infoMaskBitmap != null) {
            infoMaskBitmap.recycle();
        }

        infoMaskBitmap = Bitmap
                .createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);

        this.infoMaskCanvas = new Canvas(infoMaskBitmap);
        this.infoMaskCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (infoEditorCircle != null) {
            this.infoMaskCanvas.drawColor(info_MaskColor);
            infoEditorCircle.draw(this.infoMaskCanvas, eraser);
            infoEditorCircle.drawOutLine(this.infoMaskCanvas, outerPaint);
            infoEditorCircle.drawCenter(this.infoMaskCanvas, centerPaint, centerRadius);
        }

        if (bmp_display_rect != null && isShowDebugDraw) {
            Paint displayPaint = new Paint();
            displayPaint.setColor(bmp_displayMaskColor);
            displayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            this.infoMaskCanvas.drawRect(bmp_display_rect, displayPaint);
        }

        canvas.drawBitmap(infoMaskBitmap, 0, 0, null);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {

        if (infoEditorCircle == null) {
            return super.onTouchEvent(event);
        }
        MatrixPosition matrixPosition = new MatrixPosition(contentImageView);
        float          xT             = event.getX();
        float          yT             = event.getY();
        int            xV             = infoEditorCircle.getPoint().x;
        int            yV             = infoEditorCircle.getPoint().y;
        int            radius         = infoEditorCircle.getRadius();
        double         dx             = Math.pow(xT - xV, 2);
        double         dy             = Math.pow(yT - yV, 2);
        boolean        isTouchOnFocus = (dx + dy) <= Math.pow(radius, 2);
        boolean        isTouchCenter  = (dx + dy) <= Math.pow(centerRadius, 2);

        if (!isTouchOnFocus && !touchRadius) {
            return super.onTouchEvent(event);
        }

        PointF bmpPos;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isTouchOnFocus) {
                    targetView.getView().setPressed(true);
                    targetView.getView().invalidate();
                }
                if (!isTouchCenter) {
                    touchRadius = true;
                }

                return true;
            case MotionEvent.ACTION_UP:
                touchRadius = false;
                if (isTouchOnFocus) {
                    targetView.getView().performClick();
                    targetView.getView().setPressed(true);
                    targetView.getView().invalidate();
                    targetView.getView().setPressed(false);
                    targetView.getView().invalidate();
                }
                return true;
            case MotionEvent.ACTION_MOVE:

                targetView.setPoint(new Point((int) xT, (int) yT), infoEditorCircle.getRadius());
                bmpPos = matrixPosition
                        .getBmpPositionFormMatrix(targetView.getPoint().x, targetView.getPoint().y);
                infoPosX = (int) bmpPos.x;
                infoPosY = (int) bmpPos.y;

                infoNodeCircleRect = infoEditorCircle.getRectF();
                updateInfoDescription();

                return true;
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("Touch Event", "ACTION_OUTSIDE");
                return true;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.d("Touch Event", "ACTION_HOVER_MOVE");
                return true;
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.d("Touch Event", "ACTION_HOVER_ENTER");
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void dismissInfoNode() {

        dismissInfoCircle();
        dismissInfoEditCircle();
        this.setVisibility(View.INVISIBLE);
    }

    public void showInfoCircle(Bitmap source) {

        Bitmap rectBitmap;

        this.setVisibility(View.VISIBLE);

        rectBitmap = Utils
                .getBmpRectangleBySource(source, infoPosX - infoRadius, infoPosY - infoRadius, infoRadius * 2, infoRadius * 2);

        infoNodeCircleBitmap = Utils.getBmpCenterCircleBySource(rectBitmap, infoRadius);
        Utils.drawOutCircle(infoNodeCircleBitmap);

        if (infoNodeCircleView != null) {
            dismissInfoCircle();
        }

        infoNodeCircleView = new ImageView(this.getContext());
        infoNodeCircleView.setImageBitmap(infoNodeCircleBitmap);

        this.addView(infoNodeCircleView);

        setEditorDisplayMode();
        updateInfoCircle();
    }

    private void setEditorDisplayMode() {

        if (this.isEditor) {
            infoEditorAddImage.setVisibility(VISIBLE);
        } else {
            infoEditorAddImage.setVisibility(INVISIBLE);
        }
    }

    private void dismissInfoCircle() {

        if (infoNodeCircleView != null) {
            this.removeView(infoNodeCircleView);
            infoNodeCircleView = null;
        }
    }

    private void dismissInfoEditCircle() {

        targetView = null;
        infoEditorCircle = null;
    }

    public void showInfoEditCircle() {

        dismissInfoNode();

        InfoNodeContentView.this.setVisibility(View.VISIBLE);

        MatrixPosition matrixPosition = new MatrixPosition(contentImageView);
        PointF         mPos           = matrixPosition.getMatrixPosition(infoPosX, infoPosY);
        Point          Pos            = new Point((int) mPos.x, (int) mPos.y);

        targetView = new ViewTarget(contentImageView);
        infoEditorCircle = new Circle(targetView, infoRadius);

        infoEditorCircle.setRadius((int) (infoRadius * matrixPosition.getMatrixScaleX()));
        targetView.setPoint(Pos, (int) (infoRadius * matrixPosition.getMatrixScaleX()));

        setEditorDisplayMode();
        updateInfoEditCircle();
    }

    private void updateInfoEditCircle() {

        if (infoEditorCircle == null) {
            return;
        }

        MatrixPosition matrixPosition = new MatrixPosition(contentImageView);

        PointF mPos = matrixPosition.getMatrixPosition(infoPosX, infoPosY);
        Point  Pos  = new Point((int) mPos.x, (int) mPos.y);

        infoEditorCircle.setRadius((int) (infoRadius * matrixPosition.getMatrixScaleX()));
        targetView.setPoint(Pos, (int) (infoRadius * matrixPosition.getMatrixScaleX()));

        infoNodeCircleRect = infoEditorCircle.getRectF();
        centerRadius = (int) (matrixPosition.getMatrixHeight() * 0.05);

        updateInfoDescription();
        this.invalidate();
    }

    public void scaleView(View v, float startScale, float endScale) {

        AnimatorSet   animatorSet = new AnimatorSet();
        ValueAnimator scaleX      = ObjectAnimator.ofFloat(v, View.SCALE_X, endScale);
        scaleX.setDuration(1000);
        ValueAnimator scaleY = ObjectAnimator.ofFloat(v, View.SCALE_Y, endScale);
        scaleY.setDuration(1000);
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();
    }

    @Override public void onClick(View v) {

        if (v.getId() == R.id.image_view_icon) {
            MaterialDialog.InputCallback inputInfo = new MaterialDialog.InputCallback() {
                @Override public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    infoDescription = input.toString();
                    if (isAddMode) {
                        infoDescription = input.toString();
                        InfoNodeContentView.this.setVisibility(View.INVISIBLE);
                        actionListener.onInfoAddFinishEdit();
                    } else {
                        updateInfoView();
                    }
                }
            };

            DialogUtil.showInputDialog(this.getContext(), AppResource
                    .getString(R.string.infonode_description), AppResource
                                               .getString(R.string.please_input), "", infoDescription, AppResource
                                               .getString(R.string.confirm_ok), AppResource
                                               .getString(R.string.confirm_cancel), 2, AppResource
                                               .getValue(R.integer.infonode_description_string_max), inputInfo);
        }
    }

    public OviewInfoNodeModel makeInfoNodeModelByBitmapIndex(int index) {

        PointF             mPos;
        OviewInfoNodeModel model          = new OviewInfoNodeModel();
        MatrixPosition     matrixPosition = new MatrixPosition(contentImageView);
        mPos = matrixPosition
                .getBmpPositionFormMatrix(targetView.getPoint().x, targetView.getPoint().y);

        Size size = new Size((int) matrixPosition.getBitmapWidth(), (int) matrixPosition
                .getBitmapHeight());

        float Radius       = Utils.radiusLengthToPercent(infoRadius, size);
        float posPercent[] = Utils.pointToPercent(mPos, size);

        model.setN(String.valueOf(index));
        model.setInfo(this.infoDescription);
        model.setR(String.valueOf(Radius));
        model.setX(String.valueOf(posPercent[0]));
        model.setY(String.valueOf(posPercent[1]));
        return model;
    }

    public void setActionListener(OnInfoNodeEventListener actionListener) {

        this.actionListener = actionListener;
    }

    public interface OnInfoNodeEventListener {
        boolean onInfoAddFinishEdit();
    }
}
