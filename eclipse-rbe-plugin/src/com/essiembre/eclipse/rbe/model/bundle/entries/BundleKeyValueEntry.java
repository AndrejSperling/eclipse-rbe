package com.essiembre.eclipse.rbe.model.bundle.entries;

public class BundleKeyValueEntry extends BundleValueEntry {
	
	private String key;
	private boolean isCommented;
	
	public BundleKeyValueEntry(final String key, final String value, final boolean isCommented) {
		super(value);
		this.key = key;
		this.isCommented = isCommented;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	public boolean isCommented() {
		return this.isCommented;
	}
	
	public void setCommented(boolean isCommented) {
		if(this.isCommented != isCommented) {
			setModified();
		}
		this.isCommented = isCommented;
	}

	@Override
	public String getLine() {
		final String prefix = (isCommented) ? "# " : "";
		return prefix + key + " = " + getValue();
	}



}
