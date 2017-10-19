package akronix.es.biciparkmadrid.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by akronix on 19/10/17.
 */

public class SquareImageView extends android.support.v7.widget.AppCompatImageView{

    public SquareImageView(Context context) {
        super(context);
    }
    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
