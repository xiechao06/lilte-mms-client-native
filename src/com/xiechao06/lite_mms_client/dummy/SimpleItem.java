package com.xiechao06.lite_mms_client.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
//public class SimpleItem {
//
//	/**
//	 * An array of sample (dummy) items.
//	 */
//	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
//
//	/**
//	 * A map of sample (dummy) items, by ID.
//	 */
//	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
//
//	static {
//		// Add 3 sample items.
//		addItem(new DummyItem("1", "Item 1"));
//		addItem(new DummyItem("2", "Item 2"));
//		addItem(new DummyItem("3", "Item 3"));
//	}
//
//	private static void addItem(DummyItem item) {
//		ITEMS.add(item);
//		ITEM_MAP.put(item.id, item);
//	}
//
//	/**
//	 * A dummy item representing a piece of content.
//	 */
//	public static class DummyItem {
//		public String id;
//		public String content;
//
//		public DummyItem(String id, String content) {
//			this.id = id;
//			this.content = content;
//		}
//
//		@Override
//		public String toString() {
//			return content;
//		}
//	}
//}

public class SimpleItem {
	public String id;
	public String content;
	private boolean mChecked;

	public SimpleItem(String id, String content) {
		this.id = id;
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}
	
	public void setChecked(boolean checked) {
		mChecked = checked;
	}

	public boolean isChecked() {
		return mChecked;
	}
}
