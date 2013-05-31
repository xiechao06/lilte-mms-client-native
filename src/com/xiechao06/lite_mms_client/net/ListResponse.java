package com.xiechao06.lite_mms_client.net;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> {
	private boolean hasMore;
	private int totalCnt;
	private List<T> data;
	
	public ListResponse(boolean hasNext, int totalCnt) {
		this.hasMore = hasNext;
		this.totalCnt = totalCnt;
		this.data = new ArrayList<T>();
	}
	public boolean hasMore() {
		return hasMore;
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public List<T> getData() {
		return data;
	}
	public void addItem(T item) {
		this.data.add(item);
	}

}
