package cn.edu.hit.facelock;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GestureLockView extends View {
	private Paint paintNormal;
	private Paint paintOnTouch;
	private Paint paintInnerCycle;
	private Paint paintLines;
	private Paint paintKeyError;
	private MyCycle[] cycles;
	private Path linePath = new Path();
	private List<Integer> linedCycles = new ArrayList<Integer>();
	private OnGestureFinishListener onGestureFinishListener;
	private String key;
	private int eventX, eventY;
	private boolean canContinue = true;
	private boolean result;
	private Timer timer;
	
	private int OUT_CYCLE_NORMAL    = Color.rgb(108, 119, 138);        // 正常外圆颜色
	private int OUT_CYCLE_ONTOUCH   = Color.rgb(025, 066, 103);        // 选中外圆颜色
	private int INNER_CYCLE_ONTOUCH = Color.rgb(002, 210, 255);        // 选择内圆颜色
	private int LINE_COLOR          = Color.argb(127, 002, 210, 255);  // 连接线颜色
	private int ERROR_COLOR         = Color.argb(127, 255, 000, 000);  // 连接错误醒目提示颜色
	
	public void setOnGestureFinishListener(
			OnGestureFinishListener onGestureFinishListener) {
		this.onGestureFinishListener = onGestureFinishListener;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public interface OnGestureFinishListener {
		public void OnGestureFinish(boolean success);
	}

	public GestureLockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GestureLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GestureLockView(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int perSize = 0;
		if (cycles == null && (perSize = getWidth() / 6) > 0) {
			cycles = new MyCycle[9];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					MyCycle cycle = new MyCycle();
					cycle.setNum(i * 3 + j);
					cycle.setOx(perSize * (j * 2 + 1));
					cycle.setOy(perSize * (i * 2 + 1));
					cycle.setR(perSize * 0.5f);
					cycles[i * 3 + j] = cycle;
				}
			}
		}
	}

	private void init() {
		paintNormal = new Paint();
		paintNormal.setAntiAlias(true);
		paintNormal.setStrokeWidth(3);
		paintNormal.setStyle(Paint.Style.STROKE);
		
		paintOnTouch = new Paint();
		paintOnTouch.setAntiAlias(true);
		paintOnTouch.setStrokeWidth(3);
		paintOnTouch.setStyle(Paint.Style.STROKE);
		
		paintInnerCycle = new Paint();
		paintInnerCycle.setAntiAlias(true);
		paintInnerCycle.setStyle(Paint.Style.FILL);
		
		paintLines = new Paint();
		paintLines.setAntiAlias(true);
		paintLines.setStyle(Paint.Style.STROKE);
		paintLines.setStrokeWidth(6);
		
		paintKeyError = new Paint();
		paintKeyError.setAntiAlias(true);
		paintKeyError.setStyle(Paint.Style.STROKE);
		paintKeyError.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < cycles.length; i++) {
			if (!canContinue && !result) {
				paintOnTouch.setColor(ERROR_COLOR);
				paintInnerCycle.setColor(ERROR_COLOR);
				paintLines.setColor(ERROR_COLOR);
			} else if (cycles[i].isOnTouch()) {
				paintOnTouch.setColor(OUT_CYCLE_ONTOUCH);
				paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
				paintLines.setColor(LINE_COLOR);
			} else {
				paintNormal.setColor(OUT_CYCLE_NORMAL);
				paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
				paintLines.setColor(LINE_COLOR);
			}
			if (cycles[i].isOnTouch()) {
				canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(), cycles[i].getR(), paintOnTouch);
				// drawInnerBlueCycle
				drawInnerBlueCycle(cycles[i], canvas);
			} else {
				canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(), cycles[i].getR(), paintNormal);
			}
		}
		// drawLine
		drawLine(canvas);
	}
	
	
	private void drawLine(Canvas canvas) {
		linePath.reset();
		if (linedCycles.size() > 0) {
			for (int i = 0; i < linedCycles.size(); i++) {
				int index = linedCycles.get(i);
				if (i == 0) {
					linePath.moveTo(cycles[index].getOx(), cycles[index].getOy());
				} else {
					linePath.lineTo(cycles[index].getOx(), cycles[index].getOy());
				}
			}
			linePath.lineTo(eventX, eventY);
			canvas.drawPath(linePath, paintLines);
		}
	}

	private void drawInnerBlueCycle(MyCycle myCycle, Canvas canvas) {
		canvas.drawCircle(myCycle.getOx(), myCycle.getOy(), myCycle.getR() / 3, paintInnerCycle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canContinue) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE: {
				eventX = (int) event.getX();
				eventY = (int) event.getY();
				for (int i = 0; i < cycles.length; i++) {
					if (cycles[i].isPointIn(eventX, eventY)) {
						cycles[i].setOnTouch(true);
						if (!linedCycles.contains(cycles[i].getNum())) {
							linedCycles.add(cycles[i].getNum());
						}
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				// 暂停触碰
				canContinue = false;
				// 检查结果
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < linedCycles.size(); i++) {
					sb.append(linedCycles.get(i));
				}
				result = key.equals(sb.toString());
				if (onGestureFinishListener != null) {
					onGestureFinishListener.OnGestureFinish(result);
				}
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// 还原
						eventX = eventY = 0;
						for (int i = 0; i < cycles.length; i++) {
							cycles[i].setOnTouch(false);
						}
						linedCycles.clear();
						linePath.reset();
						canContinue = true;
						postInvalidate();
					}
				}, 1000);
				break;
			}
			}
			invalidate();
		}
		return true;
	}
}
