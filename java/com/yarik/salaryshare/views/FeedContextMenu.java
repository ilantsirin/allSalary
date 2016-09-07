package com.yarik.salaryshare.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yarik.salaryshare.R;
import com.yarik.salaryshare.model.Salary;
import com.yarik.salaryshare.utils.UtilHelper;

/**
 * Created by Michael on 2/24/2015.
 */

public class FeedContextMenu extends LinearLayout {
    private final int CONTEXT_MENU_WIDTH = UtilHelper.dpToPx(240);
    private Salary salary;

    private Button reportButton;
    private Button shareButton;
    private Button favoritesButton;
    private Button cancelButton;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public FeedContextMenu(Context context, boolean favorited) {
        super(context);
        init(favorited);
    }

    private void init(boolean favorited) {
       View v = LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
       reportButton = (Button) v.findViewById(R.id.btnReport);
       shareButton = (Button) v.findViewById(R.id.btnShare);
       favoritesButton = (Button) v.findViewById(R.id.btnFavorite);
       cancelButton = (Button) v.findViewById(R.id.btnCancel);

        if (favorited) {
            favoritesButton.setText("UNFAVORITE");
        } else {
            favoritesButton.setText("FAVORITE");
        }

        reportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onReportClick(salary);
                }
            }
        });

        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onShareClick(salary);
                }
            }
        });

        favoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onFavoritesClick(salary);
                }
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onCancelClick(salary);
                }
            }
        });

        //setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(Salary salary) {
        this.salary = salary;
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }


    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onReportClick(Salary salary);

        public void onShareClick(Salary salary);

        public void onFavoritesClick(Salary salary);

        public void onCancelClick(Salary salary);
    }

}