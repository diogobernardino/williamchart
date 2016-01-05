package com.db.williamchartdemo;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.BaseBarChartView;
import com.db.chart.view.BaseStackBarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.HorizontalStackBarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.BounceEase;
import com.db.chart.view.animation.easing.CircEase;
import com.db.chart.view.animation.easing.CubicEase;
import com.db.chart.view.animation.easing.ElasticEase;
import com.db.chart.view.animation.easing.ExpoEase;
import com.db.chart.view.animation.easing.LinearEase;
import com.db.chart.view.animation.easing.QuadEase;
import com.db.chart.view.animation.easing.QuartEase;
import com.db.chart.view.animation.easing.QuintEase;
import com.db.chart.view.animation.easing.SineEase;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ValueBar;

import java.text.DecimalFormat;


public class SandboxFragment extends Fragment {

    private SandboxPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private final static int DEFAULT_COLOR = -9276814;

    private static BaseSandBoxFragment mCurrFragment;

    /** Layout views to keep */
    private static ImageButton mPlayBtn;

    /** Labels and values */
    private final static String[] mLabels= {"W", "I", "L", "L", "I", "A", "M"};
    private final static float[] mValues = {2.5f, 3.7f, 4f, 8f, 4.5f, 4f, 5f};

    /** Animation end action */
    private static final Runnable mEndAction = new Runnable() {
        @Override
        public void run() {
            mPlayBtn.setEnabled(true);
        }
    };

    /** Animation order */
    private final static int[] mEqualOrder = {0, 1, 2, 3, 4, 5, 6};
    private final static int[] mMiddleOrder = {3, 2, 4, 1, 5, 0, 6};
    private final static int[] mLastOrder = {6, 5, 4, 3, 2, 1, 0};

    /** IDs */
    private static int mChartId;
    private static int mLabelXId;
    private static int mAxisXId;
    private static int mLabelYId;
    private static int mAxisYId;
    private static int mEasingId;
    private static int mOrderId;
    private static int mEnterId;
    private static int mAlphaId;
    private static int mGridTypeId;
    private static int mGridLineTypeId;
    private static int mGridThicknessId;
    private static int mLineTypeId;
    private static int mLineThicknessTypeId;
    private static int mLineThicknessId;
    private static int mPointsSizeId;
    private static int mBarSpacingId;
    private static int mBarCornersSizeId;

    /** Chart */
    private static int mChartColorId;

    /** Axis */
    private static boolean mHasYAxis;
    private static AxisController.LabelPosition mYLabelPosition;
    private static boolean mHasXAxis;
    private static AxisController.LabelPosition mXLabelPosition;
    private static int mAxisColorId;
    private static int mLabelColorId;
    private static String mLabelFormat;

    /** Grid */
    private static ChartView.GridType mGridType;
    private static Paint mGridPaint;
    private static boolean mIsGridDashed;
    private static float[] mGridDashType;
    private static float mGridThickness;
    private static int mGridColorId;

    /** Line */
    private static boolean mIsLineSmooth;
    private static boolean mIsLineDashed;
    private static float[] mLineDashType;
    private static float mLineThickness;
    private static float mPointsSize;
    private static int mLineColorId;
    private static int mPointColorId;

    /** Bar */
    private static float mBarSpacing;
    private static int mBarColorId;
    private static float mBarCornersSize;
    private static boolean mHasBarBackground;
    private static int mBarBackgroundId;
    private static int mBarBackgroundColorId;

    /** Animation */
    private static int mDuration;
    private static float mOverlapFactor;
    private static int[] mOverlapOrder;
    private static BaseEasingMethod mEasing;
    private static float mStartX;
    private static float mStartY;
    private static int mAlpha;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mLayout = inflater.inflate(R.layout.sandbox, container, false);

        Toolbar toolbar = (Toolbar) mLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Pager
        mSectionsPagerAdapter = new SandboxPagerAdapter(this.getChildFragmentManager());

        mViewPager = (ViewPager) mLayout.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //getActivity().setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) mLayout.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);


        // Defaults
        mChartId = R.id.sandbox_chart_line;
        mChartColorId = 1;

        mLabelXId = R.id.sandbox_axis_x_outside;
        mAxisXId = R.id.sandbox_axis_x_axis;
        mLabelYId = R.id.sandbox_axis_y_outside;
        mAxisYId = R.id.sandbox_axis_y_axis;
        mAxisColorId = DEFAULT_COLOR;
        mLabelColorId = DEFAULT_COLOR;
        mLabelFormat = "";

        mOrderId = R.id.sandbox_anim_ordere;
        mEnterId = R.id.sandbox_anim_enterb;
        mAlphaId = -1;

        mGridTypeId = -1;
        mGridLineTypeId = R.id.sandbox_grid_solid;
        mGridThicknessId = R.id.sandbox_grid_thickness3;

        mLineTypeId = -1;
        mLineThicknessTypeId = R.id.sandbox_line_solid;
        mLineThicknessId = R.id.sandbox_line_thickness3;
        mLineColorId = DEFAULT_COLOR;
        mPointColorId = DEFAULT_COLOR;

        mBarSpacing = 10;
        mBarCornersSize = 0;
        mBarColorId = DEFAULT_COLOR;
        mHasBarBackground = false;
        mBarBackgroundId = -1;
        mBarBackgroundColorId = DEFAULT_COLOR;
        mBarSpacingId = R.id.sandbox_bar_spacing1;
        mBarCornersSizeId = R.id.sandbox_bar_corner1;

        mHasYAxis = true;
        mYLabelPosition = AxisController.LabelPosition.OUTSIDE;
        mHasXAxis = true;
        mXLabelPosition = AxisController.LabelPosition.OUTSIDE;

        mGridType = null;
        mIsGridDashed = false;
        mGridDashType = null;
        mGridThickness = 1f;
        mGridThicknessId = R.id.sandbox_grid_thickness3;
        mGridColorId = DEFAULT_COLOR;

        mIsLineSmooth = false;
        mIsLineDashed = false;
        mLineDashType = null;
        mLineThickness = 3;
        mLineThicknessId = R.id.sandbox_line_thickness3;
        mPointsSize = 0;
        mPointsSizeId = -1;

        mDuration = 1000;
        mOverlapFactor = 1;
        mOverlapOrder = mEqualOrder;
        mEasingId = 0;
        mEasing = new CubicEase();
        mStartX = -1;
        mStartY = 0;
        mAlpha = -1;

        return mLayout;
    }


    private static ChartView buildLineChart(LineChartView chart){

        chart.reset();

        LineSet dataset = new LineSet(mLabels, mValues);

        if(mIsLineDashed)
            dataset.setDashed(mLineDashType);
        dataset.setSmooth(mIsLineSmooth)
                .setThickness(Tools.fromDpToPx(mLineThickness))
                .setColor(mLineColorId);

        if(mPointsSizeId != -1)
            dataset.setDotsRadius(Tools.fromDpToPx(mPointsSize))
                    .setDotsColor(mPointColorId);
        chart.addData(dataset);

        return chart;
    }


    private String buildLineCode(){

        StringBuilder code = new StringBuilder();

        code.append("\n// Line chart customization\n");

        code.append("LineSet dataset = new LineSet(<labels>, <values>);\n");

        if(mIsLineDashed)
            code.append("dataset.setDashed(true);\n");

        if(mIsLineSmooth)
            code.append("dataset.setSmooth(true);\n");

        code.append("dataset.setThickness(Tools.fromDpToPx(" + mLineThickness + "));\n");
        if(mLineColorId != DEFAULT_COLOR)
            code.append("dataset.setColor(Color.parseColor('#").append(Integer.toHexString(mLineColorId).substring(2)).append("'));\n");

        if(mPointsSizeId != -1)
            code.append("dataset.setDotsRadius(Tools.fromDpToPx(").append(mPointsSize).append("));\n");
        if(mPointColorId != DEFAULT_COLOR)
            code.append("dataset.setDotsColor(Color.parseColor('#").append(Integer.toHexString(mPointColorId).substring(2)).append("'));\n");

        code.append("chart.addData(dataset);\n");

        return code.toString();
    }


    private static ChartView buildBarChart(BaseBarChartView chart){

        chart.reset();

        BarSet dataset = new BarSet(mLabels, mValues);
        dataset.setColor(mBarColorId);
        chart.addData(dataset);

        chart.setBarSpacing(mBarSpacing);
        if(mHasBarBackground)
            chart.setBarBackgroundColor(mBarBackgroundColorId);
        chart.setRoundCorners(mBarCornersSize);

        return chart;
    }

    private String buildBarCode(){

        StringBuilder code = new StringBuilder();

        code.append("\n// Bar chart customization\n");

        code.append("BarSet dataset = new BarSet(<labels>, <values>);\n");
        if(mBarColorId != DEFAULT_COLOR)
            code.append("dataset.setColor(Color.parseColor('#").append(Integer.toHexString(mBarColorId).substring(2)).append("'));\n");
        if(mBarSpacing != 0)
            code.append("chart.setBarSpacing(").append(mBarSpacing).append(");\n");
        if(mHasBarBackground)
            code.append("chart.setBarBackgroundColor(Color.parseColor('#").append(Integer.toHexString(mBarBackgroundColorId).substring(2)).append("'));\n");

        code.append("chart.addData(dataset);\n");

        return code.toString();
    }


    private static ChartView buildStackedChart(BaseStackBarChartView chart){

        chart.reset();

        BarSet dataset = new BarSet(mLabels, mValues);
        dataset.setColor(mBarColorId);
        chart.addData(dataset);

        chart.setBarSpacing(mBarSpacing);
        if(mHasBarBackground)
            chart.setBarBackgroundColor(mBarBackgroundColorId);
        chart.setRoundCorners(mBarCornersSize);

        return chart;
    }

    private String buildStackedCode(){

        StringBuilder code = new StringBuilder();

        code.append("\n// Stacked chart customization\n");

        code.append("BarSet dataset = new BarSet(<labels>, <values>);\n");
        if(mBarColorId != DEFAULT_COLOR)
            code.append("dataset.setColor(Color.parseColor('# ").append(Integer.toHexString(mBarColorId).substring(2)).append("'));\n");
        if(mBarSpacing != 0)
            code.append("chart.setBarSpacing(").append(mBarSpacing).append(");\n");
        if(mHasBarBackground)
            code.append("chart.setBarBackground(Color.parseColor('#").append(Integer.toHexString(mBarBackgroundColorId).substring(2)).append("'));\n");

        code.append("chart.addData(dataset);\n");

        return code.toString();
    }


    private static void buildChart(ChartView chart){

        mGridPaint =  new Paint();
        mGridPaint.setColor(mGridColorId);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStrokeWidth(Tools.fromDpToPx(mGridThickness));
        if(mIsGridDashed)
            mGridPaint.setPathEffect(new DashPathEffect(mGridDashType, 0));

        chart.setXAxis(mHasXAxis)
                .setXLabels(mXLabelPosition)
                .setYAxis(mHasYAxis)
                .setYLabels(mYLabelPosition)
                .setLabelsColor(mLabelColorId)
                .setAxisColor(mAxisColorId);

        if(mGridType != null)
            chart.setGrid(mGridType, mGridPaint);

        chart.setLabelsFormat(new DecimalFormat("#"+mLabelFormat));

        chart.show(buildAnimation());
    }

    private String buildCode(){

        StringBuilder code = new StringBuilder();

        code.append("// Do not assume the code below as final. " +
                "For a complete customization and well behaviour of your " +
                "chart please check the documentation, wiki, and code examples.\n");

        switch(mChartId){
            case R.id.sandbox_chart_line:
                code.append("LineChartView chart = (LineChartView) <layout>.findViewById(<chart_id>);\n");
                code.append(buildLineCode());
                break;
            case R.id.sandbox_chart_bar:
                code.append("BarChartView chart = (BarChartView) <layout>.findViewById(<chart_id>);\n");
                code.append(buildBarCode());
                break;
            case R.id.sandbox_chart_horbar:
                code.append("HorizontalBarChartView chart = (HorizontalBarChartView) <layout>.findViewById(<chart_id>);\n");
                code.append(buildBarCode());
                break;
            case R.id.sandbox_chart_stacked:
                code.append("StackBarChartView chart = (StackBarChartView) <layout>.findViewById(<chart_id>);\n");
                code.append(buildStackedCode());
                break;
            case R.id.sandbox_chart_horstacked:
                code.append("HorizontalStackChartView chart = (HorizontalStackChartView) <layout>.findViewById(<chart_id>);\n");
                code.append(buildStackedCode());
                break;
            default:
        }


        code.append("\n// Generic chart customization\n");

        if(!mHasXAxis) code.append("chart.setXAxis(false);\n");
        if(!mHasYAxis) code.append("chart.setYAxis(false);\n");
        if((mHasXAxis || mHasYAxis) && mAxisColorId !=  DEFAULT_COLOR)
            code.append("chart.setAxisColor(Color.parseColor('#").append(Integer.toHexString(mAxisColorId).substring(2)).append("'));\n");
        if(mXLabelPosition != AxisController.LabelPosition.OUTSIDE)
            code.append("chart.setXLabels(").append(mXLabelPosition).append(");\n");
        if(mYLabelPosition != AxisController.LabelPosition.OUTSIDE)
            code.append("chart.setYLabels(").append(mYLabelPosition).append(");\n");
        if((mXLabelPosition != AxisController.LabelPosition.NONE || mYLabelPosition != AxisController.LabelPosition.NONE)
                && mLabelColorId !=  DEFAULT_COLOR)
            code.append("chart.setLabelsColor(Color.parseColor('#").append(Integer.toHexString(mLabelColorId).substring(2)).append("'));\n");
        if(mGridType != null) {
            code.append("// Paint object used to draw Grid\n" + "Paint gridPaint = new Paint();\n" + "gridPaint.setColor(Color.parseColor('#").append(Integer.toHexString(mGridColorId).substring(2)).append("'));\n").append("gridPaint.setStyle(Paint.Style.STROKE);\n").append("gridPaint.setAntiAlias(true);\n").append("gridPaint.setStrokeWidth(Tools.fromDpToPx(").append(mGridThickness).append("));\n");
            if (mIsGridDashed)
                code.append("gridPaint.setPathEffect(new DashPathEffect(").append(mGridDashType).append(", 0));\n");
            code.append("chart.setGrid(ChartView.GridType.").append(mGridType).append(", gridPaint);\n");
        }

        if(mLabelFormat != "")
            code.append("chart.setLabelsFormat(new DecimalFormat('#'+").append(mLabelFormat).append("));\n");

        code.append(buildAnimationCode());

        code.append("chart.show(anim);");

        return code.toString();
    }


    private static void generateChart(){

        mPlayBtn.setEnabled(false);

        switch(mChartId){
            case R.id.sandbox_chart_line:
                buildChart(buildLineChart((LineChartView) mCurrFragment.getView().findViewById(mChartId)));
                break;
            case R.id.sandbox_chart_bar:
                buildChart(buildBarChart((BarChartView) mCurrFragment.getView().findViewById(mChartId)));
                break;
            case R.id.sandbox_chart_horbar:
                buildChart(buildBarChart((HorizontalBarChartView) mCurrFragment.getView().findViewById(mChartId)));
            break;
            case R.id.sandbox_chart_stacked:
                buildChart(buildStackedChart((StackBarChartView) mCurrFragment.getView().findViewById(mChartId)));
                break;
            case R.id.sandbox_chart_horstacked:
                buildChart(buildStackedChart((HorizontalStackBarChartView) mCurrFragment.getView().findViewById(mChartId)));
                break;
            default:
                buildChart(buildLineChart((LineChartView) mCurrFragment.getView().findViewById(mChartId)));
        }
    }



    private static Animation buildAnimation(){

        switch (mEasingId){
            case 0: mEasing = new CubicEase(); break;
            case 1: mEasing = new QuartEase(); break;
            case 2: mEasing = new QuintEase(); break;
            case 3: mEasing = new BounceEase(); break;
            case 4: mEasing = new ElasticEase(); break;
            case 5: mEasing = new ExpoEase(); break;
            case 6: mEasing = new CircEase(); break;
            case 7: mEasing = new QuadEase(); break;
            case 8: mEasing = new SineEase(); break;
            case 9: mEasing = new LinearEase(); break;
            default: mEasing = new CubicEase();
        }

        return new Animation(mDuration)
                .setAlpha(mAlpha)
                .setEasing(mEasing)
                .setOverlap(mOverlapFactor, mOverlapOrder)
                .setStartPoint(mStartX, mStartY)
                .setEndAction(mEndAction);
    }

    private static String buildAnimationCode(){

        StringBuilder code = new StringBuilder("\n// Animation customization\n");

        if(mDuration != 1000)
            code.append("Animation anim = new Animation(").append(mDuration).append(");\n");
        else
            code.append("Animation anim = new Animation();\n");

        switch (mEasingId){
            case 0: code.append("anim.setEasing(new CubicEase());\n"); break;
            case 1: code.append("anim.setEasing(new QuartEase());\n"); break;
            case 2: code.append("anim.setEasing(new QuintEase());\n"); break;
            case 3: code.append("anim.setEasing(new BounceEase());\n"); break;
            case 4: code.append("anim.setEasing(new ElasticEase());\n"); break;
            case 5: code.append("anim.setEasing(new ExpoEase());\n"); break;
            case 6: code.append("anim.setEasing(new CircEase());\n"); break;
            case 7: code.append("anim.setEasing(new QuadEase());\n"); break;
            case 8: code.append("anim.setEasing(new SineEase());\n"); break;
            case 9: code.append("anim.setEasing(new LinearEase());\n"); break;
            default:
        }

        if(mAlpha != -1)
            code.append("anim.setAlpha(").append(mAlpha).append(");\n");

        if(mOverlapOrder[0] != 0 || mOverlapFactor != 1){
            String order = "{ ";
            for(int i = 0; i < mOverlapOrder.length; i++)
                order += mOverlapOrder[i]+", ";
            order +="}";
            code.append("anim.setOverlap(").append(mOverlapFactor).append(", ").append(order).append(");\n");
        }

        if(mStartX != -1 && mStartY != 0)
            code.append("anim.setStartPoint(").append(mStartX).append(", ").append(mStartY).append(");\n");

        return code.toString();
    }



    void onMenuClick(View view){
        mCurrFragment.onStateChange(view.getId());
    }

    void onPlay(View view){

        if(view.getId() == R.id.sandbox_play_play) {
            generateChart();
        }else{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, buildCode());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Send code"));
        }
    }



    public static class SandboxPagerAdapter extends FragmentStatePagerAdapter {

        public SandboxPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            switch (position){
                case 0:
                    return new SandBoxChartFragment();
                case 1: return new SandBoxAxisFragment();
                case 2: return new SandBoxGridFragment();
                case 3:
                    if(mChartId == R.id.sandbox_chart_line)
                        return new SandBoxLineFragment();
                    else
                        return new SandBoxBarFragment();
                case 4: return new SandBoxAnimationFragment();
                case 5: return new SandBoxPlayFragment();
                default: return new SandBoxChartFragment();
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrFragment = ((BaseSandBoxFragment) object);
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chart";
                case 1:
                    return "Axis";
                case 2:
                    return "Grid";
                case 3:
                    return "Data";
                case 4:
                    return "Animation";
                case 5:
                    return "Play";
                default:
                    return "";
            }
        }
    }


    public abstract static class BaseSandBoxFragment extends Fragment {

        public void onStart(){
            super.onStart();
            onStateRecover();
        }

        void onStateRecover(){}

        void onStateChange(int id){}

        /**
         * Swap the state of Attr's Views
         * @param parentView - Parent View of views to be changed
         * @param oldId - ID of last view selected
         * @param newId - ID of new view selected
         * @param mandatoryAttr - If at least one view is required to be selected
         * @return   The ID of the new selected View
         */
        static int swapState(View parentView, int oldId, int newId, boolean mandatoryAttr){

            if(oldId == newId && !mandatoryAttr) {
                parentView.findViewById(newId).setSelected(!parentView.findViewById(newId).isSelected());
                newId = -1;

            }else if (oldId == -1) {
                parentView.findViewById(newId).setSelected(!parentView.findViewById(newId).isSelected());

            }else if (oldId != newId) {
                parentView.findViewById(oldId).setSelected(false);
                parentView.findViewById(newId).setSelected(!parentView.findViewById(newId).isSelected());
            }

            return newId;
        }

        static void configColorPicker(View view, int colorId){

            ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
            ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);
            picker.addValueBar(valueBar);
            picker.setOldCenterColor(colorId);
            picker.setColor(colorId);
        }
    }


    public static class SandBoxChartFragment extends BaseSandBoxFragment {

        private  View mLayout;
        private  AlertDialog.Builder mDialogBuilder;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mLayout = inflater.inflate(R.layout.sandbox_chart, container, false);
            mLayout.findViewById(mChartId).setSelected(true);
            mDialogBuilder = new AlertDialog.Builder(getActivity()).setNegativeButton("Cancel", null);
            return mLayout;
        }

        @Override
        protected void onStateRecover(){
            mLayout.findViewById(mChartId).setSelected(true);
        }

        @Override
        protected void onStateChange(int id) {
                mChartId = swapState(mLayout, mChartId, id, true);
        }

    }


    public static class SandBoxAxisFragment extends BaseSandBoxFragment {

        private  View mLayout;
        private  ViewGroup mViewGroup;
        private  AlertDialog.Builder mDialogBuilder;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mLayout = inflater.inflate(R.layout.sandbox_axis, container, false);
            mViewGroup = container;

            final EditText editText = (EditText) mLayout.findViewById(R.id.sandbox_axis_label_format_value);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    mLabelFormat = editText.getText().toString();
                }
            });

            mDialogBuilder = new AlertDialog.Builder(getActivity()).setNegativeButton("Cancel", null);

            return mLayout;
        }

        @Override
        protected void onStateRecover(){

            if(mLabelXId != -1)
                mLayout.findViewById(mLabelXId).setSelected(true);
            if(mAxisXId != -1)
                mLayout.findViewById(mAxisXId).setSelected(true);
            if(mLabelYId != -1)
                mLayout.findViewById(mLabelYId).setSelected(true);
            if(mAxisYId != -1)
                mLayout.findViewById(mAxisYId).setSelected(true);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_axis_color)).getBackground())
                    .setColor(mAxisColorId);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_label_color)).getBackground())
                    .setColor(mLabelColorId);
        }

        @Override
        protected void onStateChange(int id){

            switch(id) {

                /** Label Y **/
                case R.id.sandbox_axis_y_outside:
                    if(id == mLabelYId)
                        mYLabelPosition = AxisController.LabelPosition.NONE;
                    else
                        mYLabelPosition = AxisController.LabelPosition.OUTSIDE;
                    mLabelYId = swapState(mLayout, mLabelYId, id, false);
                    break;
                case R.id.sandbox_axis_y_inside:
                    if(id == mLabelYId)
                        mYLabelPosition = AxisController.LabelPosition.NONE;
                    else
                        mYLabelPosition = AxisController.LabelPosition.INSIDE;
                    mLabelYId = swapState(mLayout, mLabelYId, id, false);
                    break;

                /** Axis Y **/
                case R.id.sandbox_axis_y_axis:
                    mHasYAxis = !mHasYAxis;
                    mAxisYId = swapState(mLayout, mAxisYId, id, false);
                    break;

                /** Label X **/
                case R.id.sandbox_axis_x_inside:
                    if(id == mLabelXId)
                        mXLabelPosition = AxisController.LabelPosition.NONE;
                    else
                        mXLabelPosition = AxisController.LabelPosition.INSIDE;
                    mLabelXId = swapState(mLayout, mLabelXId, id, false);
                    break;
                case R.id.sandbox_axis_x_outside:
                    if(id == mLabelXId)
                        mXLabelPosition = AxisController.LabelPosition.NONE;
                    else
                        mXLabelPosition = AxisController.LabelPosition.OUTSIDE;
                    mLabelXId = swapState(mLayout, mLabelXId, id, false);
                    break;

                /** Axis X **/
                case R.id.sandbox_axis_x_axis:
                    mHasXAxis = !mHasXAxis;
                    mAxisXId = swapState(mLayout, mAxisXId, id, false);
                    break;

                case R.id.sandbox_axis_color:
                    final View axisColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(axisColorLayout, mAxisColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    ColorPicker picker = (ColorPicker) axisColorLayout.findViewById(R.id.picker);
                                    mAxisColorId = picker.getColor();
                                    ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_axis_color)).getBackground())
                                            .setColor(mAxisColorId);

                                }
                            })
                            .setView(axisColorLayout)
                            .create()
                            .show();
                    break;

                case R.id.sandbox_label_color:
                    final View labelColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(labelColorLayout, mLabelColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    ColorPicker picker = (ColorPicker) labelColorLayout.findViewById(R.id.picker);
                                    mLabelColorId = picker.getColor();
                                    ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_label_color)).getBackground())
                                            .setColor(mLabelColorId);
                                }
                            })
                            .setView(labelColorLayout)
                            .create()
                            .show();
                    break;

                default:
            }
        }
    }


    public static class SandBoxGridFragment extends BaseSandBoxFragment {

        private  View mLayout;
        private  ViewGroup mViewGroup;
        private  AlertDialog.Builder mDialogBuilder;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mViewGroup = container;
            mDialogBuilder = new AlertDialog.Builder(getActivity()).setNegativeButton("Cancel", null);
            return mLayout = inflater.inflate(R.layout.sandbox_grid, container, false);
        }

        @Override
        protected void onStateRecover(){

            if(mGridTypeId != -1)
                mLayout.findViewById(mGridTypeId).setSelected(true);
            mLayout.findViewById(mGridLineTypeId).setSelected(true);
            mLayout.findViewById(mGridThicknessId).setSelected(true);
        }

        @Override
        protected void onStateChange(int id){

            switch (id){

                /** Grid Type **/
                case R.id.sandbox_grid_hor:
                    if(mGridTypeId == id)
                        mGridType = ChartView.GridType.NONE;
                    else
                        mGridType = ChartView.GridType.HORIZONTAL;
                    mGridTypeId = swapState(mLayout, mGridTypeId, id, false);
                    break;
                case R.id.sandbox_grid_ver:
                    if(mGridTypeId == id)
                        mGridType = ChartView.GridType.NONE;
                    else
                        mGridType = ChartView.GridType.VERTICAL;
                    mGridTypeId = swapState(mLayout, mGridTypeId, id, false);
                    break;
                case R.id.sandbox_grid_full:
                    if(mGridTypeId == id)
                        mGridType = ChartView.GridType.NONE;
                    else
                        mGridType = ChartView.GridType.FULL;
                    mGridTypeId = swapState(mLayout, mGridTypeId, id, false);
                    break;

                /** Grid line type **/
                case R.id.sandbox_grid_solid:
                    mIsGridDashed = false;
                    mGridLineTypeId = swapState(mLayout, mGridLineTypeId, id, true);
                    break;
                case R.id.sandbox_grid_dashed:
                    mIsGridDashed = true;
                    mGridDashType = new float[] {10, 10};
                    mGridLineTypeId = swapState(mLayout, mGridLineTypeId, id, true);
                    break;
                case R.id.sandbox_grid_dashed2:
                    mIsGridDashed = true;
                    mGridDashType = new float[] {5, 5};
                    mGridLineTypeId = swapState(mLayout, mGridLineTypeId, id, true);
                    break;

                /** Grid line thickness **/
                case R.id.sandbox_grid_thickness1:
                    mGridThickness = Tools.fromDpToPx(4);
                    mGridThicknessId = swapState(mLayout, mGridThicknessId, id, true);
                    break;
                case R.id.sandbox_grid_thickness2:
                    mGridThickness = Tools.fromDpToPx(3);
                    mGridThicknessId = swapState(mLayout, mGridThicknessId, id, true);
                    break;
                case R.id.sandbox_grid_thickness3:
                    mGridThickness = Tools.fromDpToPx(2);
                    mGridThicknessId = swapState(mLayout, mGridThicknessId, id, true);
                    break;
                case R.id.sandbox_grid_thickness4:
                    mGridThickness = Tools.fromDpToPx(1);
                    mGridThicknessId = swapState(mLayout, mGridThicknessId, id, true);
                    break;

                case R.id.sandbox_grid_color:
                    final View gridColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(gridColorLayout, mGridColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ColorPicker picker = (ColorPicker) gridColorLayout.findViewById(R.id.picker);
                                mGridColorId = picker.getColor();
                                ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_grid_color)).getBackground())
                                        .setColor(mGridColorId);
                            }
                        })
                        .setView(gridColorLayout)
                        .create()
                        .show();
                    break;

                default:
            }
        }
    }


    public static class SandBoxLineFragment extends BaseSandBoxFragment {

        private  View mLayout;
        private  ViewGroup mViewGroup;
        private  AlertDialog.Builder mDialogBuilder;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mViewGroup = container;
            mDialogBuilder = new AlertDialog.Builder(getActivity()).setNegativeButton("Cancel", null);
            return mLayout = inflater.inflate(R.layout.sandbox_line, container, false);
        }

        @Override
        protected void onStateRecover(){

            if(mLineTypeId != -1)
                mLayout.findViewById(mLineTypeId).setSelected(true);
            mLayout.findViewById(mLineThicknessTypeId).setSelected(true);
            mLayout.findViewById(mLineThicknessId).setSelected(true);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_line_color)).getBackground())
                    .setColor(mLineColorId);
            if(mPointsSizeId != -1)
                mLayout.findViewById(mPointsSizeId).setSelected(true);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_point_color)).getBackground())
                    .setColor(mPointColorId);
        }

        @Override
        protected void onStateChange(int id) {

            switch(id) {

                /** Line type **/
                case R.id.sandbox_line_smooth:
                    mIsLineSmooth = !mIsLineSmooth;
                    mLineTypeId = swapState(mLayout, mLineTypeId, id, false);
                    break;

                /** Line type 2 **/
                case R.id.sandbox_line_solid:
                    mIsLineDashed = false;
                    mLineThicknessTypeId = swapState(mLayout, mLineThicknessTypeId, id, true);
                    break;
                case R.id.sandbox_line_dashed:
                    mIsLineDashed = true;
                    mLineDashType = new float[] {10, 10};
                    mLineThicknessTypeId = swapState(mLayout, mLineThicknessTypeId, id, true);
                    break;
                case R.id.sandbox_line_dashed2:
                    mIsLineDashed = true;
                    mLineDashType = new float[] {5, 5};
                    mLineThicknessTypeId = swapState(mLayout, mLineThicknessTypeId, id, true);
                    break;

                /** Line thickness **/
                case R.id.sandbox_line_thickness1:
                    mLineThickness = Tools.fromDpToPx(7);
                    mLineThicknessId = swapState(mLayout, mLineThicknessId, id, true);
                    break;
                case R.id.sandbox_line_thickness2:
                    mLineThickness = Tools.fromDpToPx(5);
                    mLineThicknessId = swapState(mLayout, mLineThicknessId, id, true);
                    break;
                case R.id.sandbox_line_thickness3:
                    mLineThickness = Tools.fromDpToPx(3);
                    mLineThicknessId = swapState(mLayout, mLineThicknessId, id, true);
                    break;
                case R.id.sandbox_line_thickness4:
                    mLineThickness = Tools.fromDpToPx(1);
                    mLineThicknessId = swapState(mLayout, mLineThicknessId, id, true);
                    break;

                case R.id.sandbox_line_color:
                    final View lineColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(lineColorLayout, mLineColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ColorPicker picker = (ColorPicker) lineColorLayout.findViewById(R.id.picker);
                            mLineColorId = picker.getColor();
                            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_line_color)).getBackground())
                                    .setColor(mLineColorId);

                        }
                    })
                            .setView(lineColorLayout)
                            .create()
                            .show();
                    break;

                /** Points size **/
                case R.id.sandbox_point_size1:
                    mPointsSize = Tools.fromDpToPx(8);
                    mPointsSizeId = swapState(mLayout, mPointsSizeId, id, false);
                    break;
                case R.id.sandbox_point_size2:
                    mPointsSize = Tools.fromDpToPx(6);
                    mPointsSizeId = swapState(mLayout, mPointsSizeId, id, false);
                    break;
                case R.id.sandbox_point_size3:
                    mPointsSize = Tools.fromDpToPx(4);
                    mPointsSizeId = swapState(mLayout, mPointsSizeId, id, false);
                    break;
                case R.id.sandbox_point_size4:
                    mPointsSize = Tools.fromDpToPx(2);
                    mPointsSizeId = swapState(mLayout, mPointsSizeId, id, false);
                    break;

                /** Points color **/
                case R.id.sandbox_point_color:
                    final View pointColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(pointColorLayout, mPointColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ColorPicker picker = (ColorPicker) pointColorLayout.findViewById(R.id.picker);
                            mPointColorId = picker.getColor();
                            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_point_color)).getBackground())
                                    .setColor(mPointColorId);
                        }
                    })
                            .setView(pointColorLayout)
                            .create()
                            .show();
                    break;

                default:
            }
        }
    }


    public static class SandBoxBarFragment extends BaseSandBoxFragment {

        private  View mLayout;
        private  ViewGroup mViewGroup;
        private  AlertDialog.Builder mDialogBuilder;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mViewGroup = container;
            mDialogBuilder = new AlertDialog.Builder(getActivity()).setNegativeButton("Cancel", null);
            mLayout = inflater.inflate(R.layout.sandbox_bar, container, false);

            return mLayout;
        }

        @Override
        protected void onStateRecover() {
            mLayout.findViewById(mBarCornersSizeId).setSelected(true);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_bar_color)).getBackground())
                    .setColor(mBarColorId);
            mLayout.findViewById(mBarSpacingId).setSelected(true);
            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_bar_background_color)).getBackground())
                    .setColor(mBarBackgroundColorId);
            if(mBarBackgroundId != -1)
                mLayout.findViewById(mBarSpacingId).setSelected(true);
        }

        @Override
        protected void onStateChange(int id) {


            switch (id){
                case R.id.sandbox_bar_color:
                    final View barColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, null, false);
                    configColorPicker(barColorLayout, mBarColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ColorPicker picker = (ColorPicker) barColorLayout.findViewById(R.id.picker);
                            mBarColorId = picker.getColor();
                            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_bar_color)).getBackground())
                                    .setColor(mBarColorId);

                        }
                    })
                            .setView(barColorLayout)
                            .create()
                            .show();
                    break;

                /** Corners size **/
                case R.id.sandbox_bar_corner1:
                    mBarCornersSize = Tools.fromDpToPx(0);
                    mBarCornersSizeId = swapState(mLayout, mBarCornersSizeId, id, false);
                    break;
                case R.id.sandbox_bar_corner2:
                    mBarCornersSize = Tools.fromDpToPx(4);
                    mBarCornersSizeId = swapState(mLayout, mBarCornersSizeId, id, false);
                    break;
                case R.id.sandbox_bar_corner3:
                    mBarCornersSize = Tools.fromDpToPx(8);
                    mBarCornersSizeId = swapState(mLayout, mBarCornersSizeId, id, false);
                    break;
                case R.id.sandbox_bar_corner4:
                    mBarCornersSize = Tools.fromDpToPx(12);
                    mBarCornersSizeId = swapState(mLayout, mBarCornersSizeId, id, false);
                    break;

                /** Bar spacing **/
                case R.id.sandbox_bar_spacing1:
                    mBarSpacing = Tools.fromDpToPx(10);
                    mBarSpacingId = swapState(mLayout, mBarSpacingId, id, true);
                    break;
                case R.id.sandbox_bar_spacing2:
                    mBarSpacing = Tools.fromDpToPx(16);
                    mBarSpacingId = swapState(mLayout, mBarSpacingId, id, true);
                    break;
                case R.id.sandbox_bar_spacing3:
                    mBarSpacing = Tools.fromDpToPx(22);
                    mBarSpacingId = swapState(mLayout, mBarSpacingId, id, true);
                    break;
                case R.id.sandbox_bar_spacing4:
                    mBarSpacing = Tools.fromDpToPx(28);
                    mBarSpacingId = swapState(mLayout, mBarSpacingId, id, true);
                    break;

                /** Bar background color */
                case R.id.sandbox_bar_background_toggle:
                    mHasBarBackground = !mHasBarBackground;
                    mBarBackgroundId = swapState(mLayout, mBarBackgroundId, id, false);
                    break;

                case R.id.sandbox_bar_background_color:
                    final View backgroundColorLayout = getActivity().getLayoutInflater().inflate(R.layout.color_picker, mViewGroup, false);
                    configColorPicker(backgroundColorLayout, mBarBackgroundColorId);
                    mDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ColorPicker picker = (ColorPicker) backgroundColorLayout.findViewById(R.id.picker);
                            mBarBackgroundColorId = picker.getColor();
                            ((GradientDrawable) (mLayout.findViewById(R.id.sandbox_bar_background_color)).getBackground())
                                    .setColor(mBarBackgroundColorId);
                        }
                    })
                            .setView(backgroundColorLayout)
                            .create()
                            .show();
                    break;

                default:
            }

        }
    }


    public static class SandBoxAnimationFragment extends BaseSandBoxFragment {

        private View mLayout;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mLayout = inflater.inflate(R.layout.sandbox_animation, container, false);
            Spinner spinner = (Spinner) mLayout.findViewById(R.id.sandbox_anim_easing_type);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                    R.array.easing, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            /** Easing function **/
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    mEasingId = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}

            });

            Spinner spinnerDuration = (Spinner) mLayout.findViewById(R.id.sandbox_anim_duration);
            ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this.getActivity(),
                    R.array.duration, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDuration.setAdapter(adapterDuration);

            /** Duration function **/
            spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                final String[] durations = getResources().getStringArray(R.array.duration);

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    mDuration = Integer.parseInt(durations[position].replace("ms", ""));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}

            });

            return mLayout;
        }

        @Override
        protected void onStateRecover(){

            mLayout.findViewById(mOrderId).setSelected(true);
            mLayout.findViewById(mEnterId).setSelected(true);
            if(mAlphaId != -1)
                mLayout.findViewById(mAlphaId).setSelected(true);
        }

        protected void onStateChange(int id){

            switch(id) {

                /** Alpha value **/
                case R.id.sandbox_anim_alpha1:
                    if(id == mAlphaId)
                        mAlpha = -1;
                    else
                        mAlpha = 1;
                    mAlphaId = swapState(mLayout, mAlphaId, id, false);
                    break;
                case R.id.sandbox_anim_alpha2:
                    if(id == mAlphaId)
                        mAlpha = -1;
                    else
                        mAlpha = 2;
                    mAlphaId = swapState(mLayout, mAlphaId, id, false);
                    break;
                case R.id.sandbox_anim_alpha3:
                    if(id == mAlphaId)
                        mAlpha = -1;
                    else
                        mAlpha = 3;
                    mAlphaId = swapState(mLayout, mAlphaId, id, false);
                    break;

                /** Animation order **/
                case R.id.sandbox_anim_ordere:
                    mOverlapFactor = 1;
                    mOverlapOrder = mEqualOrder;
                    mOrderId = swapState(mLayout, mOrderId, id, true);
                    break;
                case R.id.sandbox_anim_orderf:
                    mOverlapFactor = .5f;
                    mOverlapOrder = mEqualOrder;
                    mOrderId = swapState(mLayout, mOrderId, id, true);
                    break;
                case R.id.sandbox_anim_orderl:
                    mOverlapFactor = .5f;
                    mOverlapOrder = mLastOrder;
                    mOrderId = swapState(mLayout, mOrderId, id, true);
                    break;
                case R.id.sandbox_anim_orderm:
                    mOverlapFactor = .5f;
                    mOverlapOrder = mMiddleOrder;
                    mOrderId = swapState(mLayout, mOrderId, id, true);
                    break;

                /** Enter point **/
                case R.id.sandbox_anim_entertl:
                    mStartX = 0f;
                    mStartY = 1f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_entert:
                    mStartX = -1f;
                    mStartY = 1f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_entertr:
                    mStartX = 1f;
                    mStartY = 1f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_entercl:
                    mStartX = 0f;
                    mStartY = -1f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_enterc:
                    mStartX = .5f;
                    mStartY = .5f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_entercr:
                    mStartX = 1f;
                    mStartY = -1f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_enterbl:
                    mStartX = 0f;
                    mStartY = 0f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_enterb:
                    mStartX = -1f;
                    mStartY = 0f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;
                case R.id.sandbox_anim_enterbr:
                    mStartX = 1f;
                    mStartY = 0f;
                    mEnterId = swapState(mLayout, mEnterId, id, true);
                    break;

                default:
            }
        }
    }


    public static class SandBoxPlayFragment extends BaseSandBoxFragment {

        private View mLayout;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mLayout = inflater.inflate(R.layout.sandbox_play, container, false);
            mPlayBtn = (ImageButton) mLayout.findViewById(R.id.sandbox_play_play);
            return mLayout;
        }

        @Override
        protected void onStateRecover(){
            mLayout.findViewById(mChartId).setVisibility(View.VISIBLE);
        }

    }

}
