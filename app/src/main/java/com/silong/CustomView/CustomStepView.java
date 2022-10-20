package com.silong.CustomView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.baoyachi.stepview.VerticalStepView;
import com.silong.dev.R;

import java.util.Arrays;
import java.util.List;

public class CustomStepView extends VerticalStepView {

    private List<String> sources = Arrays.asList(getResources().getStringArray(R.array.steps));

    public CustomStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.reverseDraw(false)
                .setStepViewTexts(sources)
                .setLinePaddingProportion(0.65f)
                //complete
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(context, R.color.pink))
                .setStepViewComplectedTextColor(ContextCompat.getColor(context, R.color.black))
                .setStepsViewIndicatorCompleteIcon(context.getDrawable(R.drawable.task_complete))
                //uncompleted
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(context, R.color.gray))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(context,R.color.gray))
                .setStepsViewIndicatorAttentionIcon(context.getDrawable(R.drawable.task_ongoing_1))
                //default
                .setStepsViewIndicatorDefaultIcon(context.getDrawable(R.drawable.task_icon))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(context, R.color.gray))
                .setTextSize(13);
    }
}
