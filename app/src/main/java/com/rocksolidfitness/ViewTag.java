package com.rocksolidfitness;

public class ViewTag
{
    int groupPosition;
    int childPosition;
    boolean isLastChildInGroup;
    Session session;

    public ViewTag(int groupPosition,
                   int childPosition,
                   boolean isLastChildInGroup,
                   Session session)
    {
        this.groupPosition = groupPosition;
        this.childPosition = childPosition;
        this.isLastChildInGroup = isLastChildInGroup;
        this.session = session;
    }

    public long getDateOfSessionInMillis()
    {
        return session.getDateOfSession().getMillis();
    }

    public boolean isPlaceHolder()
    {
        return session.description.equals(Consts.NO_SESSIONS_YET);
    }
}
