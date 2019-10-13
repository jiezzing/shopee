package com.example.shopee.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shopee.R;
import com.example.shopee.models.OrderHeader;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder>{
    private Context context;
    private List<OrderHeader> list;

    public OrderAdapter(Context context, List<OrderHeader> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_header, viewGroup, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
        final OrderHeader header = list.get(i);
        String order_no = header.getOrder_no();
        String status = header.getStatus();

        orderViewHolder.order_no.setText("Order # " + String.format("%05d", Integer.parseInt(order_no)));
        orderViewHolder.status.setText(status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class OrderViewHolder extends RecyclerView.ViewHolder{
    public TextView order_no, status;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        order_no = itemView.findViewById(R.id.order_no);
        status = itemView.findViewById(R.id.status);
    }
}
