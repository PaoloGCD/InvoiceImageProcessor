package com.example.ramhacks_invoicescan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentList extends Fragment {

    public List<InvoiceElement> mInvoiceList;

    public static final int FRAGMENT_INVOICES = 1;
    public static final int FRAGMENT_DOCUMENTS = 2;
    public static final int FRAGMENT_RANDOM = 3;

    private int mFragmentType;

    private ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load Current Babies List
        Context context = getActivity();
        InvoiceDbHandler dataBase = new InvoiceDbHandler(context);

        switch (mFragmentType){
            case FRAGMENT_INVOICES:
                mInvoiceList = dataBase.getInvoiceByDate();
                break;
            case FRAGMENT_DOCUMENTS:
                mInvoiceList = dataBase.getInvoiceByDate();
                break;
            case FRAGMENT_RANDOM:
                mInvoiceList = dataBase.getInvoiceByDate();
                break;
        }

        if (mInvoiceList == null){
            mInvoiceList = new ArrayList<>();
        }

        // Inflate the layout for this fragment
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        adapter = new ListAdapter(getActivity(), mInvoiceList, mFragmentType);
        rv.setAdapter(adapter);
        return rv;
    }

    public static class ListAdapter extends
            RecyclerView.Adapter<ListAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public View mView;
            public TextView primaryText;
            public TextView secondaryText;
            public TextView avatarText;

            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                primaryText = (TextView) itemView.findViewById(R.id.item_primary_text);
                secondaryText = (TextView) itemView.findViewById(R.id.item_secondary_text);
                avatarText = (TextView) itemView.findViewById(R.id.item_avatar_text);
            }
        }

        private List<InvoiceElement> mInvoiceList;
        private Context mContext;
        private int mFragmentType;

        public ListAdapter(Context context, List<InvoiceElement> mInvoices, int fragmentType) {
            mInvoiceList = mInvoices;
            mContext = context;
            mFragmentType = fragmentType;
        }

        private Context getContext() {
            return mContext;
        }

        // Involves inflating a layout from XML and returning the holder
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View babyItemView = inflater.inflate(R.layout.item_fragment_list, parent, false);

            return new ViewHolder(babyItemView);
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(final ListAdapter.ViewHolder viewHolder, final int position) {
            final InvoiceElement invoice = mInvoiceList.get(position);

            TextView primaryTextView = viewHolder.primaryText;
            TextView secondaryTextView = viewHolder.secondaryText;
            TextView avatarTextView = viewHolder.avatarText;

            primaryTextView.setText(parseDate(invoice.mDate));
            secondaryTextView.setText("$" + invoice.mCost + " (" + invoice.mItems + " items)");

            avatarTextView.setText(invoice.mStore.substring(0,1).toUpperCase());
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return mInvoiceList.size();
        }

        public String parseDate(String date_ddMMyyyy) {
            String time = date_ddMMyyyy;
            String inputPattern = "MM/dd/yy HH:mma";
            String outputPattern = "MMMM d yyyy, K:mm a";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }
    }

    public static FragmentList newInstance(int fragmentListType) {
        FragmentList myFragment = new FragmentList();

        myFragment.mFragmentType = fragmentListType;

        return myFragment;
    }

    public void addNewItem(InvoiceElement newInvoice){
        if (mInvoiceList != null){
            switch (mFragmentType){
                case FRAGMENT_INVOICES:
                    mInvoiceList.add(0,newInvoice);
                    adapter.notifyItemInserted(0);
                    break;
                case FRAGMENT_DOCUMENTS:
                    mInvoiceList.add(0,newInvoice);
                    adapter.notifyItemInserted(0);
                    break;
                case FRAGMENT_RANDOM:
                    mInvoiceList.add(0,newInvoice);
                    adapter.notifyItemInserted(0);
                    break;
            }
        }
    }

}
