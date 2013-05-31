package com.xiechao06.lite_mms_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiechao06.lite_mms_client.dummy.SimpleItem;
import com.xiechao06.lite_mms_client.net.FilterCond;
import com.xiechao06.lite_mms_client.net.ListResponse;
import com.xiechao06.lite_mms_client.net.WebService;
import com.xiechao06.lite_mms_client.utils.CommonUtils;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 * <p>
 * This fragment show the simplest form of list of Items, namely, "__repr__" of
 * the item
 */
public abstract class BaseListFragment1 extends ListFragment {

	private static final String TAG = "BaseListFragment1";

	protected static final int REFRESHING = 4;

	public interface SetListRunnable {
		public void run(ListResponse<SimpleItem> response);
	}

	private List<SimpleItem> mList;
	private static Handler webHandler;
	private int mTotalCnt;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	public static final int PAGE_SIZE = 4;
	public static final int MAX_PAGE = 2;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	protected boolean hasMore;
	private LayoutInflater mInflater;
	private ProgressBar mProgressBarLoadMore;
	private boolean mIsLoadingMore;
	private RelativeLayout mFooterView;
	private List<FilterCond> mFilterCondList;

	abstract protected String getModelName();

	protected int getPageSize() {
		return 16;
	}

	private class GetListRunnable implements Runnable {

		private String modelName;
		private int offset;
		private int limit;
		private String orderBy;
		private boolean desc;
		private Map<String, String> filters;
		private SetListRunnable runnable;

		public GetListRunnable(String modelName, int offset, int limit,
				String orderBy, boolean desc, Map<String, String> filters,
				SetListRunnable runnable) {
			this.modelName = modelName;
			this.offset = offset;
			this.limit = limit;
			this.orderBy = orderBy;
			this.desc = desc;
			this.filters = filters;
			this.runnable = runnable;
		}

		@Override
		public void run() {
			Exception ex = null;
			try {
				ListResponse<SimpleItem> ret = WebService.getInstance()
						.getList(modelName, offset, limit, orderBy, desc,
								filters);
				runnable.run(ret);
			} catch (IOException e) {
				e.printStackTrace();
				ex = e;
			} catch (NetworkErrorException e) {
				e.printStackTrace();
				ex = e;				
			} catch (JSONException e) {
				e.printStackTrace();
				ex = e;				
			}
			if (ex != null) {
				CommonUtils.makeExceptionToast(getActivity(), ex);
			}
		}
	}

	public void doGetList(String modelName, int offset, int limit,
			String orderBy, boolean desc, Map<String, String> filters,
			SetListRunnable runnable) {
		if (webHandler == null) {
			HandlerThread ht = new HandlerThread("");
			ht.start();
			webHandler = new Handler(ht.getLooper());
		}
		webHandler.post(new GetListRunnable(modelName, offset, limit, orderBy,
				desc, filters, runnable));
	}

	protected void initList(int size) {

		Map<String, String> filters = new HashMap<String, String>();

		doGetList(getModelName(), 0, size, "create_time", true, filters,
				new BaseListFragment1.SetListRunnable() {
					@Override
					public void run(ListResponse<SimpleItem> response) {
						mTotalCnt = response.getTotalCnt();
						final boolean first_load = mList.isEmpty();
						mList.clear();
						BaseListFragment1.this.mList.addAll(response.getData());
						BaseListFragment1.this.hasMore = response.hasMore();
						Log.d(TAG, "has more: " + hasMore);
						BaseListFragment1.this.getActivity().runOnUiThread(
								new Runnable() {

									@Override
									public void run() {
										try {
											if (first_load) {
												setListAdapter(new MyListViewAdapter());
											} else {
												((BaseAdapter) BaseListFragment1.this
														.getListAdapter())
														.notifyDataSetChanged();
											}
											Toast.makeText(
													getActivity(),
													String.format(
															getActivity()
																	.getResources()
																	.getString(
																			R.string.load_success_msg)
																	.toString(),
															mTotalCnt,
															mList.size()),
													Toast.LENGTH_SHORT).show();
											if (!hasMore
													|| mList.size() >= MAX_PAGE
															* PAGE_SIZE) {
												mFooterView
														.setVisibility(View.GONE);
											}
											if (mList.size() >= MAX_PAGE
													* PAGE_SIZE) {
												Toast.makeText(
														getActivity(),
														String.format(
																getActivity()
																		.getResources()
																		.getString(
																				R.string.max_page_reached),
																MAX_PAGE
																		* PAGE_SIZE),
														Toast.LENGTH_SHORT)
														.show();
											}
										} catch (Exception e) {
											e.printStackTrace();
											Toast.makeText(
													BaseListFragment1.this
															.getView()
															.getContext(),
													e.getMessage(),
													Toast.LENGTH_SHORT).show();
										}
										mIsLoadingMore = false;
										mProgressBarLoadMore
												.setVisibility(View.GONE);
									}
								});
					}
				});
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BaseListFragment1() {
		mList = new ArrayList<SimpleItem>();
		mFilterCondList = new ArrayList<FilterCond>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
		initList(PAGE_SIZE);
		mInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		// pull down to request footer
		mFooterView = (RelativeLayout) mInflater.inflate(
				R.layout.load_more_footer, getListView(), false);

		mProgressBarLoadMore = (ProgressBar) mFooterView
				.findViewById(R.id.load_more_progressBar);

		mFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickLoadMore();
			}
		});

		getListView().addFooterView(mFooterView);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.

		// mCallbacks.onItemSelected(SimpleItem.ITEMS.get(position).id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	/**
	 * Interface definition for a callback to be invoked when list reaches the
	 * last item (the user load more items in the list)
	 */
	public interface OnLoadMoreListener {
		/**
		 * Called when the list reaches the last item (the last item is visible
		 * to the user)
		 */
		public void onLoadMore();
	}

	/**
	 * clear the content and retrieve the first page again
	 */
	public void refresh() {
		initList(PAGE_SIZE);
	}

	public void onClickLoadMore() {
		if (!mIsLoadingMore) {
			initList(mList.size() + PAGE_SIZE);
			mIsLoadingMore = true;
			mProgressBarLoadMore.setVisibility(View.VISIBLE);
		}
	}

	class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			class ViewHolder {
				public CheckBox checkBox;
				public TextView textView;
			}
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.simple_list_item, null);
				ViewHolder vh = new ViewHolder();
				vh.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				vh.textView = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(vh);
				vh.checkBox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								SimpleItem item = (SimpleItem) buttonView
										.getTag();
								item.setChecked(isChecked);
								Log.d(TAG, "total selected"
										+ selectedItems().size());
							}
						});
				vh.checkBox.setTag(getItem(position));
			} else {
				ViewHolder vh = (ViewHolder) convertView.getTag();
				vh.checkBox.setTag(getItem(position));
			}
			ViewHolder vh = (ViewHolder) convertView.getTag();
			SimpleItem item = (SimpleItem) getItem(position);
			vh.checkBox.setSelected(item.isChecked());
			vh.textView.setText(item.content);
			return convertView;
		}

	}

	public List<SimpleItem> selectedItems() {
		List<SimpleItem> ret = new ArrayList<SimpleItem>();
		for (SimpleItem item : mList) {
			if (item.isChecked()) {
				ret.add(item);
			}
		}
		return ret;
	}
}
