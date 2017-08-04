package com.animator_abhi.recyclerviewcalendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Recycler calendar view.
 */
public class RecyclerCalendarView extends FrameLayout {


    // public static  String selectedDate;
    public int[] selectedDate;
    private int[] mTodayDate;

    /*date are in int array of size 3
    index 0 for year
    index 1 for month
    index 2 for day
     */
    static private int minDate[] = new int[3];
    static private int maxDate[] = new int[3];
    /**
     * List of Events contains date in integer array of size 3
     * index 0 for year
     * index 1 for month
     * index 2 for day
     */
    static private List<int[]> events;


    /**
     * List of Disable dates contains date in integer array of size 3
     * index 0 for year
     * index 1 for month
     * index 2 for day
     */
    static private List<int[]> disableDates;

    private FixedHeaderRecyclerView mCalendarRecyclerView;

    private GridLayoutManager mCalendarLayoutManager;

    private CalendarAdapter mCalendarAdapter;

    public RecyclerCalendarView(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }



/*set event*/
    public void setEvent(List<int[]> eventDates) {
        this.events = eventDates;
        updateCalendar();
    }

/*set disable date*/
    public void setDisableDates(List<int[]> disableDates) {
        setDisableDates(disableDates, false);
    }

/**
 * boolean paramenter if true than event on disable will also be of same color as disable date
 * else its colorful
 */

    public void setDisableDates(List<int[]> disableDates, boolean isEventColorDisable) {
        this.disableDates = disableDates;
        Util.getInstance().setDisableDateEventColor(isEventColorDisable);
        resetSelected();
        updateCalendar();
    }


    /* set min date*/
    public void setMinDate(int minYear, int minMonth) {

        minDate[0] = minYear;
        minDate[1] = minMonth;
        minDate[2] = 1;
        updateCalendar();
    }
    /* set min date*/
    public void setMinDate(int minYear, int minMonth, int minDay) {

        minDate[0] = minYear;
        minDate[1] = minMonth;
        minDate[2] = minDay;
        updateCalendar();
    }
    /* set max date*/
    public void setMaxDate(int maxYear, int maxMonth) {

        maxDate[0] = maxYear;
        maxDate[1] = maxMonth;
        maxDate[2] = 1;
        updateCalendar();
    }
    /* set max date*/
    public void setMaxDate(int maxYear, int maxMonth, int maxDay) {

        maxDate[0] = maxYear;
        maxDate[1] = maxMonth;
        maxDate[2] = maxDay;
        updateCalendar();
    }

    /**
     * set visibility of fixed month header
     */
    public void showMonthHeader(boolean flg) {
        if (flg == true) {
            mCalendarRecyclerView.setFixedHeaderView(R.layout.item_month);
        } else {
            mCalendarRecyclerView.setFixedHeaderView(0);
        }
    }

    /**
     * function to update the calendar data
     * call it whenever selection mode, min date, max date, events or disable date is modified or set
     *
     */

    private void updateCalendar()
    {
        resetSelected();
        mCalendarAdapter.setCalendarData(CalendarEntity.newCalendarData(mDoubleSelectedMode, mTodayDate, minDate, maxDate, events, disableDates));
    }

    public RecyclerCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Util.init(getContext());

        mTodayDate = Util.getTodayDate();

        inflate(getContext(), R.layout.view_recycler_calendar, this);

        mCalendarRecyclerView = (FixedHeaderRecyclerView) findViewById(R.id.calendar);
        // monthTextView.setTextSize(56);
        mCalendarLayoutManager = new GridLayoutManager(getContext(), 7);
        mCalendarRecyclerView.setLayoutManager(mCalendarLayoutManager);


        mCalendarAdapter = new CalendarAdapter(getContext());

        mCalendarAdapter.setOnDayClickListener(new CalendarAdapter.OnDayClickListener() {
            @Override
            void onDayClick(int position) {
                super.onDayClick(position);

                clickPosition(position, true, true);
            }
        });

        mCalendarAdapter.setOnDayLongClickListener(new CalendarAdapter.OnDayLongClickListener() {
            @Override
            void onDayLongClick(int position) {
                super.onDayLongClick(position);
                //  clickPosition(position, true, true);
                Toast.makeText(getContext(), "long click", Toast.LENGTH_SHORT).show();
            }}


        );

        mCalendarRecyclerView.setAdapter(mCalendarAdapter);

/**
 * by default selection mode is single
 */
        setDoubleSelectedMode(false);
        scrollToSelected();
    }

    //*****************************************************************************************************************
    // Select Mode.

    /**
     * if true double selection mode else, single selection  mode.
     */
    private boolean mDoubleSelectedMode;

    /**
     * The currently selected first position.
     */
    private int mSelectedPositionA = -1;
    /**
     * The currently selected second position.
     */
    private int mSelectedPositionB = -1;

    /**
     * return double mode is set or not.
     */
    public boolean isDoubleSelectedMode() {
        return mDoubleSelectedMode;
    }

    /**
     * 
     Set whether dual mode selected, Select the date and reset.
     */
    public void setDoubleSelectedMode(boolean doubleSelectedMode) {
        setDoubleSelectedMode(doubleSelectedMode, true);
    }

    /**
     * 设置单选模式, 并指定选中的日期.
     */
    public void setDoubleSelectedMode(int[] date) {
        setDoubleSelectedMode(false, false);

        clickPosition(getPosition(date), true, false);
    }

    /**
     * 设置双选模式, 并指定选中的日期.
     */
    public void setDoubleSelectedMode(int[] dateFrom, int[] dateTo) {
        setDoubleSelectedMode(true, false);

        clickPosition(getPosition(dateFrom), false, false);
        clickPosition(getPosition(dateTo), true, false);
    }

    private void setDoubleSelectedMode(boolean doubleSelectedMode, boolean notifyDataSetChanged) {
        if (mDoubleSelectedMode != doubleSelectedMode) {
            mDoubleSelectedMode = doubleSelectedMode;

            mCalendarAdapter.setCalendarData(null);
        }

        if (mCalendarAdapter.getCalendarData().isEmpty()) {


            mCalendarAdapter.setCalendarData(CalendarEntity.newCalendarData(mDoubleSelectedMode, mTodayDate, minDate, maxDate, events, disableDates));

        }


        resetSelected(notifyDataSetChanged);
    }

    /**
     * 重置选中日期.
     */
    public void resetSelected() {
        resetSelected(true);
    }

    private void resetSelected(boolean notifyDataSetChanged) {
        if (mDoubleSelectedMode) {
            unselectPositionAB();
        } else {
            selectPositionB(-1);
            selectPositionA(getPosition(mTodayDate));
        }

        if (notifyDataSetChanged) {
            mCalendarAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击某位置.
     */
    private void clickPosition(int position, boolean notifyDataSetChanged, boolean callback) {
        if (mDoubleSelectedMode) {
            // 双选.
            if (mSelectedPositionA == -1) {
                // 两个都未选中.
                selectPositionB(-1);
                selectPositionA(position);
                if (callback) {
                    onDoubleFirstSelected(mSelectedPositionA);
                }
            } else if (mSelectedPositionB == -1) {
                // 已选中第一个.
                if (position == mSelectedPositionA) {
                    // 要取消选中第一个.
                    selectPositionA(-1);
                    if (callback) {
                        onDoubleFirstUnselected(position);
                    }
                } else {
                    // 要选中第二个.
                    int selectedCount = getPositionABSelectedCount(mSelectedPositionA, position);
                    if (selectedCount <= Util.getInstance().max_double_selected_count) {
                        selectPositionAB(mSelectedPositionA, position);
                        if (callback) {
                            onDoubleSelected(mSelectedPositionA, mSelectedPositionB, selectedCount);
                        }
                    } else {
                        if (callback) {
                            onExceedMaxDoubleSelectedCount(selectedCount);
                        }
                    }
                }
            } else {
                // 两个都已选中.
                unselectPositionAB();
                selectPositionA(position);
                if (callback) {
                    onDoubleFirstSelected(mSelectedPositionA);
                }
            }
        } else {
            selectPositionA(position);
            if (callback) {
                onSingleSelected(mSelectedPositionA);
            }
        }

        if (notifyDataSetChanged) {
            mCalendarAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置第一个位置.
     */
    private void selectPositionA(int position) {
        if (mSelectedPositionA == position) {
            return;
        }

        if (mSelectedPositionA != -1) {
            setPositionSelected(mSelectedPositionA, CalendarEntity.SELECTED_TYPE_UNSELECTED);
            mSelectedPositionA = -1;
        }

        if (position == -1) {
            return;
        }

        setPositionSelected(position, CalendarEntity.SELECTED_TYPE_SELECTED);
        mSelectedPositionA = position;
    }

    /**
     * 设置第二个位置.
     */
    private void selectPositionB(int position) {
        if (mSelectedPositionB == position) {
            return;
        }

        if (mSelectedPositionB != -1) {
            setPositionSelected(mSelectedPositionB, CalendarEntity.SELECTED_TYPE_UNSELECTED);
            mSelectedPositionB = -1;
        }

        if (position == -1) {
            return;
        }

        setPositionSelected(position, CalendarEntity.SELECTED_TYPE_SELECTED);
        mSelectedPositionB = position;
    }

    /**
     * 返回两个位置的选中天数.
     */
    private int getPositionABSelectedCount(int positionA, int positionB) {
        if (positionA == -1 || positionB == -1) {
            return 0;
        }

        int fromPosition = Math.min(positionA, positionB);
        int toPosition = Math.max(positionA, positionB);

        int selectedCount = 0;
        for (int i = fromPosition; i <= toPosition; i++) {
            if (mCalendarAdapter.getCalendarEntity(i).itemType == CalendarEntity.ITEM_TYPE_DAY) {
                ++selectedCount;
            }
        }

        return selectedCount;
    }

    /**
     * 取消双选选中.
     */
    private void unselectPositionAB() {
        if (mSelectedPositionA != -1 && mSelectedPositionB != -1) {
            for (int i = mSelectedPositionA; i <= mSelectedPositionB; i++) {
                setPositionSelected(i, CalendarEntity.SELECTED_TYPE_UNSELECTED);
            }

            mSelectedPositionA = -1;
            mSelectedPositionB = -1;

            return;
        }

        selectPositionA(-1);
        selectPositionB(-1);
    }

    /**
     * 双选选中.
     */
    private void selectPositionAB(int positionA, int positionB) {
        if (positionA == -1 || positionB == -1) {
            return;
        }

        int fromPosition = Math.min(positionA, positionB);
        int toPosition = Math.max(positionA, positionB);

        selectPositionA(fromPosition);
        selectPositionB(toPosition);

        for (int i = fromPosition + 1; i < toPosition; i++) {
            setPositionSelected(i, CalendarEntity.SELECTED_TYPE_RANGED);
        }
    }

    /**
     * 设置位置的选中状态.
     */
    private void setPositionSelected(int position, int selected) {
        CalendarEntity calendarEntity = mCalendarAdapter.getCalendarData().get(position);
        if (calendarEntity.itemType == CalendarEntity.ITEM_TYPE_DAY) {
            calendarEntity.selectedType = selected;
        }
    }

    /**
     * 返回指定日期的位置, 如果没找到则返回 -1.
     */
    private int getPosition(int[] date) {
        for (int position = 0; position < mCalendarAdapter.getCalendarData().size(); position++) {
            CalendarEntity calendarEntity = mCalendarAdapter.getCalendarData().get(position);
            if (calendarEntity.itemType == CalendarEntity.ITEM_TYPE_DAY
                    && Util.isDateEqual(calendarEntity.date, date)) {
                return position;
            }
        }

        return -1;
    }

    //*****************************************************************************************************************
    // 滚动.

    /**
     * 滚动到的位置, 如果为 -1 则不滚动.
     */
    private int mScrollToPosition = -1;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        scrollToPosition(mScrollToPosition);
    }

    /**
     * 滚动到今天.
     */
    public void scrollToToday() {
        scrollToPosition(getPosition(mTodayDate));
    }

    /**
     * 滚动到选中的位置, 如果没有选中的位置则滚动到今天.
     */
    public void scrollToSelected() {
        if (mDoubleSelectedMode && mSelectedPositionA != -1) {
            if (mSelectedPositionB == -1) {
                scrollToPosition(mSelectedPositionA);
            } else {
                scrollToPosition(Math.min(mSelectedPositionA, mSelectedPositionB));
            }
        } else if (!mDoubleSelectedMode && mSelectedPositionA != -1) {
            scrollToPosition(mSelectedPositionA);
        } else {
            scrollToToday();
        }
    }

    /**
     * 滚动到指定的位置, 如果为 -1 则不滚动.
     */
    private void scrollToPosition(int position) {
        mScrollToPosition = position;

        int calendarRecyclerViewMeasuredHeight = mCalendarRecyclerView.getMeasuredHeight();
        if (mScrollToPosition == -1 || calendarRecyclerViewMeasuredHeight == 0) {
            return;
        }

        int offset = calendarRecyclerViewMeasuredHeight / 2;
        mCalendarLayoutManager.scrollToPositionWithOffset(mScrollToPosition, offset);
        mScrollToPosition = -1;
    }

    //*****************************************************************************************************************
    // 回调.

    /**
     * 单选回调.
     */
    public void onSingleSelected(int position) {
        CalendarEntity calendarEntity = mCalendarAdapter.getCalendarEntity(position);
        Toast.makeText(getContext(), Util.getDateString(calendarEntity.date), Toast.LENGTH_SHORT).show();
        selectedDate = calendarEntity.date;
    }

    /**
     * 双选回调.
     */
    private void onDoubleSelected(int positionFrom, int positionTo, int dayCount) {
        CalendarEntity calendarEntityFrom = mCalendarAdapter.getCalendarEntity(positionFrom);
        CalendarEntity calendarEntityTo = mCalendarAdapter.getCalendarEntity(positionTo);
        Toast.makeText(getContext(), Util.getDateString(calendarEntityFrom.date) + "~" +
                Util.getDateString(calendarEntityTo.date) + "," + dayCount, Toast.LENGTH_SHORT).show();
    }

    /**
     * 双选选中第一个日期回调.
     */
    private void onDoubleFirstSelected(int position) {
        CalendarEntity calendarEntity = mCalendarAdapter.getCalendarEntity(position);
        Toast.makeText(getContext(), "First select:" + Util.getDateString(calendarEntity.date), Toast.LENGTH_SHORT).show();
    }

    /**
     * 双选取消第一个日期回调.
     */
    private void onDoubleFirstUnselected(int position) {
        CalendarEntity calendarEntity = mCalendarAdapter.getCalendarEntity(position);
        Toast.makeText(getContext(), "First Deselect:" + Util.getDateString(calendarEntity.date), Toast.LENGTH_SHORT).show();
    }

    /**
     * 超过最大双选天数回调.
     */
    private void onExceedMaxDoubleSelectedCount(int dayCount) {
        Toast.makeText(getContext(), "" + dayCount, Toast.LENGTH_SHORT).show();
    }


    public void setBgColor(int color) {
        mCalendarRecyclerView.setBackgroundColor(color);
    }


    public TextView getHeaderTextView()

    {
        return mCalendarRecyclerView.getFixedHeader();

    }

    public View getFixedHeaderView() {
        return mCalendarRecyclerView.getFixedHeaderView();
    }

 /*   public void setHeaderTextSize(float size)
    {
      //  mCalendarRecyclerView.setHeaderSize(size);
        requestLayout();
    }*/

    public void setFixedHeaderColor(int color) {
        mCalendarRecyclerView.setFixedHeaderColor(color);

    }

    public int[] getTodayDate() {
        return mTodayDate;
    }

    public int[] getSelectedDate() {
        return selectedDate;
    }

    public void setWeekendDayColor(int color) {
        Util.getInstance().setText_weekend(color);
        requestLayout();

    }

    public void setEventColor(int color) {
        Util.getInstance().setText_special(color);
        requestLayout();

    }


    public void setSelectionDayColor(int color) {
        Util.getInstance().setText_selected(color);
        requestLayout();

    }

    /*  public void setBackgroundDayColor(int color)
      {
          Util.getInstance().setBackground_day(color);
          requestLayout();
      }*/
    public void setDayColor(int color) {
        Util.getInstance().setText_day(color);
        requestLayout();
    }

    public void setBackgroundRangeColor(int color) {
        Util.getInstance().setBackground_ranged(color);
        requestLayout();
    }

    public void setSelectedDayBackgroundColor(int color) {
        Util.getInstance().setBackground_selected(color);
        requestLayout();
    }

    public void setDisableDayColor(int color) {
        Util.getInstance().setText_disabled(color);
        requestLayout();
    }

    public void setTodayColor(int color) {
        Util.getInstance().setText_today(color);
        requestLayout();
    }

    public void setDecoratorItem(int decorator) {
        Util.getInstance().setDecorator(decorator);
        requestLayout();
    }

    public void resetCalendar() {
        minDate[0] = 0;
        minDate[2] = 0;
        minDate[1] = 0;
        maxDate[0] = 0;
        maxDate[1] = 0;
        maxDate[2] = 0;
        if (disableDates != null) {
            disableDates.clear();
        }
        if (events != null) {
            events.clear();
        }

        Util.getInstance().resetUtil(getContext());
        updateCalendar();
    }

    public void setMonthDividerVisible(boolean val) {
        Util.getInstance().setDividerVisibility(val);
    }

    public void setDividerColor(int color) {
        setDividerColor(color, false, true, false);
    }

    public void setDividerColor(int color, boolean top, boolean middle, boolean bottom) {
        boolean[] b = new boolean[3];
        b[0] = top;
        b[1] = middle;
        b[2] = bottom;

        Util.getInstance().setDividerColor(color, b);
    }

    public void setMonthTextViewSize(float size) {
        Util.getInstance().setMonthSize(size);
        updateCalendar();


    }

}
