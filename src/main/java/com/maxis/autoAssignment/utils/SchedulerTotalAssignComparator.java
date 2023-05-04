package com.maxis.autoAssignment.utils;

import java.util.Comparator;

import com.maxis.autoAssignment.pojo.SchAssignment;


public class SchedulerTotalAssignComparator implements Comparator<SchAssignment>
{
    public SchedulerTotalAssignComparator()
    {
    }

    public int compare(SchAssignment objX, SchAssignment objY)
    {
        if (objX != null && objY != null)
        {
            if (objX.getTotalAssigned().intValue() <= objY.getTotalAssigned().intValue())
            {
                return -1;
            }

            if (objX.getTotalAssigned().intValue() > objY.getTotalAssigned().intValue())
            {
                return 1;
            }
        }

        return 0;
    }
}