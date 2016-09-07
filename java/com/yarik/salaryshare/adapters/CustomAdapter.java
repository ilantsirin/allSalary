package com.yarik.salaryshare.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yarik.salaryshare.R;
import com.yarik.salaryshare.model.Salary;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.SalaryViewHolder>{
    private static final String TAG = "CustomAdapter";

    private Context context;
    private List<Salary> salaryList;
    private OnItemClickListener mItemClickListener;


    public CustomAdapter(Context context, List<Salary> salaryList) {
        this.context = context;
        this.salaryList = salaryList;
    }
    @Override
    public int getItemCount() {
        return salaryList.size();
    }

    @Override
    public void onBindViewHolder(CustomAdapter.SalaryViewHolder salaryViewHolder, final int i) {
        Salary salary = salaryList.get(i);
        salaryViewHolder.fieldName.setText(salary.getField());
        salaryViewHolder.posName.setText(salary.getPosition());
        salaryViewHolder.txtSalary.setText(salary.getSalary());
        salaryViewHolder.salaryDistance.setText(salary.getDistance() + " km");

        salaryViewHolder.moreButton.setTag(salary);
        salaryViewHolder.salary = salary;
        salaryViewHolder.cardview.setTag(salary);
        //Changing the background of the CardView
        /*
        if (salary.getField().equals("Arts")){
            salaryViewHolder.cardview.setCardBackgroundColor(Color.GREEN);
        }
        */
    }




    @Override
    public SalaryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.item_card, viewGroup, false);
        final SalaryViewHolder mViewHolder = new SalaryViewHolder(mView );

        return mViewHolder;
    }

    @Override
    public long getItemId(int position) {
        return salaryList.indexOf(salaryList.get(position));
    }


    public class SalaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Salary salary;
        private TextView fieldName;
        private TextView posName;
        private TextView txtSalary;
        private TextView salaryDistance;
        private ImageButton moreButton;
        private CardView cardview;
        public SalaryViewHolder(View view) {
            super(view);
            cardview = (CardView) view;
            fieldName = (TextView) view.findViewById(R.id.fieldName);
            posName = (TextView) view.findViewById(R.id.posName);
            txtSalary = (TextView) view.findViewById(R.id.txtSalary);
            salaryDistance = (TextView) view.findViewById(R.id.salaryDistance);
            moreButton = (ImageButton) view.findViewById(R.id.btnMore);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
         void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}