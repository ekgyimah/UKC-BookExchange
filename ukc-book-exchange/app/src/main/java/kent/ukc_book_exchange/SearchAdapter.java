package kent.ukc_book_exchange;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Authors: Khadija Ali, Emmanuel Gyimah
 * Class to execute the search for the books retrieved from Firebaseatabase
 */

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>  {

    public SearchAdapter(){

    }
    Context context;
    //arraylist containing books of type BookInfo
    ArrayList<BookInfo> bookNameList;

    class SearchViewHolder extends RecyclerView.ViewHolder {
        //Textview to display book name and author name
        TextView bookName, authorName;

        /**
         * Finds the bookName
         * @param itemView Takes a View as a parameter
         */
        public SearchViewHolder(View itemView) {
            super(itemView);

            bookName = (TextView) itemView.findViewById(R.id.bookName);

        }
    }

    /**
     *
     * @param context Intialises the context given
     * @param bookNameList Intialises the arrayList given
     */
    public SearchAdapter(Context context, ArrayList<BookInfo> bookNameList) {
        this.context = context;
        this.bookNameList = bookNameList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.bookName.setText(bookNameList.get(position).toString());

        holder.bookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new intent to open BuyView activity
                Intent buyView = new Intent(v.getContext(), BuyView.class);

                //Create bundle to be passed to BuyView activity
                Bundle b = new Bundle();

                //Add element to be passed in the bundle
                b.putString("title", bookNameList.get(position).getTitle());
                b.putString("author", bookNameList.get(position).getAuthor());
                b.putString("edition", bookNameList.get(position).getEdition());
                b.putString("subject", bookNameList.get(position).getSubject());
                b.putString("module", bookNameList.get(position).getModule());
                b.putString("phone", bookNameList.get(position).getPhone());
                b.putString("email", bookNameList.get(position).getEmail());

                //Add bundle to intent
                buyView.putExtras(b);

                //Start buyView activity
                context.startActivity(buyView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookNameList.size();
    }
}
