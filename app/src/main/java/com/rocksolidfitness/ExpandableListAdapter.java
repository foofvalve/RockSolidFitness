package com.rocksolidfitness;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private final Context mContext;
    private final List<String> mListDataHeader;
    private final HashMap<String, List<Session>> mListDataChild;
    SessionsDataSource dataSource;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Session>> listChildData)
    {
        mContext = context;
        mListDataHeader = listDataHeader;
        mListDataChild = listChildData;
        dataSource = new SessionsDataSource(mContext);
        dataSource.open();
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount()
    {
        return this.mListDataHeader.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition)
    {
        return this.mListDataHeader.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }


    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, parent, false);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.split("~")[0]);

        TextView lblDayOfMonth = (TextView) convertView
                .findViewById(R.id.lblDayOfMonth);
        lblDayOfMonth.setTypeface(null, Typeface.BOLD);
        lblDayOfMonth.setText(headerTitle.split("~")[1]);

        // if(headerTitle.startsWith(mContext.getString(R.string.today))&&isExpanded)
        //    lblDayOfMonth.setBackgroundColor(Color.rgb(54, 45, 99));

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent)
    {
        final Session sessionDetail = (Session) getChild(groupPosition, childPosition);
        final int groupie = groupPosition;


        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (sessionDetail.description.equals("NO_SESSIONS_YET"))
        {
            convertView = infalInflater.inflate(R.layout.list_no_session, null);
            convertView.setTag("gropie=" + groupie);
            Button addSession = (Button) convertView.findViewById(R.id.btnAddSession);
            addSession.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(mContext, "Add session button clicked", Toast.LENGTH_SHORT).show();
                }
            });


            return convertView;
        } else
            convertView = infalInflater.inflate(R.layout.list_item, null);

        convertView.setTag("gropie=" + groupie);
        TextView txtSport = (TextView) convertView
                .findViewById(R.id.lblSport);
        TextView txtDesc = (TextView) convertView.findViewById(R.id.lblDescription);
        TextView txtDuration = (TextView) convertView.findViewById(R.id.lblDuration);

        txtSport.setText(sessionDetail.sport);
        txtDesc.setText(sessionDetail.description);
        txtDuration.setText(sessionDetail.getFormattedDuration(mContext));

        boolean isSessionComplete = sessionDetail.sessionState == Session.State.COMPLETE;
        final ImageView sessionComplete = (ImageView) convertView.findViewById(R.id.imgDone);

        if (isSessionComplete)
            sessionComplete.setBackgroundColor(Color.rgb(47, 255, 64));
        else
            sessionComplete.setBackgroundColor(Color.rgb(0, 0, 0));

        ImageButton editButton = (ImageButton) convertView.findViewById(R.id.btnEdit);
        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mContext, "Edit button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                alertDialogBuilder.setTitle(mContext.getString(R.string.confirm_delete_title));

                alertDialogBuilder
                        .setMessage(mContext.getString(R.string.confirm_delete_msg))
                        .setCancelable(false)
                        .setPositiveButton(mContext.getString(R.string.confirm_delete_yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dataSource.deleteSession(sessionDetail);
                                mListDataChild.get(getGroup(groupie)).remove(sessionDetail);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, mContext.getString(R.string.toast_session_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.confirm_delete_no), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

            ;
        });

        final ImageView markAsDone = (ImageView) convertView.findViewById(R.id.imgDone);
        markAsDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sessionDetail.isComplete())
                    sessionDetail.sessionState = Session.State.PLANNED;
                else
                    sessionDetail.sessionState = Session.State.COMPLETE;

                int sessionPosition = mListDataChild.get(getGroup(groupie)).indexOf(sessionDetail);
                dataSource.updateSession(sessionDetail);
                mListDataChild.get(getGroup(groupie)).set(sessionPosition, sessionDetail);
                notifyDataSetChanged();
                Toast.makeText(mContext, mContext.getString(R.string.toast_session_state_fondled), Toast.LENGTH_SHORT).show();
            }
        });


        convertView.setOnLongClickListener(new View.OnLongClickListener()
        {

            // Defines the one method for the interface, which is called when the View is long-clicked
            public boolean onLongClick(View v)
            {

                // Create a new ClipData.
                // This is done in two steps to provide clarity. The convenience method
                // ClipData.newPlainText() can create a plain text ClipData in one step.

                // Create a new ClipData.Item from the ImageView object's tag

                ClipData dragData = ClipData.newPlainText("Group Position", "" + groupie);

                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This will create a new ClipDescription object within the
                // ClipData, and set its MIME type entry to "text/plain"
                //ClipData dragData = new ClipData(v.getTag(),ClipData.MIMETYPE_TEXT_PLAIN,item);

                // Instantiates the drag shadow builder.
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                //View.DragShadowBuilder myShadow = new MyDragShadowBuilder(imageView);

                // Starts the drag

                v.startDrag(dragData,  // the data to be dragged
                        myShadow,  // the drag shadow builder
                        null,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );
                return true;
            }
        });


        myDragEventListener mDragListen = new myDragEventListener();
        convertView.setOnDragListener(mDragListen);

        return convertView;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    ;

    protected class myDragEventListener implements View.OnDragListener
    {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.

        public boolean onDrag(View v, DragEvent event)
        {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch (action)
            {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    //if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    //{

                    // As an example of what your application might do,
                    // applies a blue color tint to the View to indicate that it can accept
                    // data.

                    v.getBackground().setColorFilter(Color.parseColor("#B7B2B0"), PorterDuff.Mode.MULTIPLY);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                //}

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                //return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    //v.setColorFilter(Color.GREEN);
                    v.getBackground().setColorFilter(Color.parseColor("#B9B9B0"), PorterDuff.Mode.MULTIPLY);
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    //v.setColorFilter(Color.BLUE);
                    v.getBackground().setColorFilter(Color.parseColor("#B7B2B0"), PorterDuff.Mode.MULTIPLY);
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    CharSequence dragData = item.getText();

                    // Displays a message containing the dragged data.

                    //Toast.makeText(mContext, "Dragged data is " + dragData, Toast.LENGTH_LONG);
                    Log.e("", "drag data is (from): " + dragData);
                    Log.e("", "view tag is (to): " + v.getTag().toString());
                    // Turns off any color tints
                    //v.clearColorFilter();
                    // v.getParent()
                    // View view = (View) event.getLocalState();


                    /*
                    ViewGroup owner = (ViewGroup) v.getParent();

                    owner.removeView(v);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(v);
                    v.setVisibility(View.VISIBLE);
                    */


                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    //v.clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult())
                    {
                        Toast.makeText(mContext, "The drop was handled.", Toast.LENGTH_LONG);

                    } else
                    {
                        Toast.makeText(mContext, "The drop didn't work.", Toast.LENGTH_LONG);

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    }
}
