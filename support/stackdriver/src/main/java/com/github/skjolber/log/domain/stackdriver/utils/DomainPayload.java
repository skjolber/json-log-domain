package com.github.skjolber.log.domain.stackdriver.utils;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DomainPayload implements AutoCloseable, Closeable {

	private static final long serialVersionUID = 1L;

	protected static final Long asLong(Object object) {
		if(object instanceof Long) {
			return (Long)object;
		}
		if(object instanceof String) {
			return Long.parseLong((String)object);
		}
		throw new IllegalArgumentException("Expected " + Long.class.getName() + " or " + String.class.getName());
	}

	protected static final Integer asInteger(Object object) {
		if(object instanceof Integer) {
			return (Integer)object;
		}
		if(object instanceof String) {
			return Integer.parseInt((String)object);
		}
		throw new IllegalArgumentException("Expected " + Integer.class.getName() + " or " + String.class.getName());
	}

	protected static final Double asDouble(Object object) {
		if(object instanceof Double) {
			return (Double)object;
		}
		if(object instanceof String) {
			return Double.parseDouble((String)object);
		}
		throw new IllegalArgumentException("Expected " + Double.class.getName() + " or " + String.class.getName());
	}
	
	protected static final Float asFloat(Object object) {
		if(object instanceof Float) {
			return (Float)object;
		}
		if(object instanceof String) {
			return Float.parseFloat((String)object);
		}
		throw new IllegalArgumentException("Expected " + Float.class.getName() + " or " + String.class.getName());
	}
	
	protected final String qualifier;
	protected DomainPayload parent;
    private List<DomainPayload> refereces;

	public DomainPayload(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void add(DomainPayload reference) {
		// limit to one level to make things a bit less complicated
		if(reference.hasReferences()) {
			throw new IllegalArgumentException("Please do not nest markers in more than one level");
		}
        if (refereces == null) {
            refereces = new ArrayList<DomainPayload>();
        }
		refereces.add(reference);
	}

	@SuppressWarnings("resource")
	public void pushContext() {
		// actual operation on this instance delegated to subclass
		if(hasReferences()) {
			for(DomainPayload reference : refereces) {
				reference.pushContext();
			}
		}
	}

	@SuppressWarnings("resource")
	public void popContext() {
		// actual operation on this instance delegated to subclass
		if(hasReferences()) {
			for(DomainPayload reference : refereces) {
				reference.popContext();
			}
		}		
	}

	@Override
	public void close() {
		popContext();
	}

	public DomainPayload getParent() { // public for testing
		return parent;
	}

	public abstract boolean equalTo(DomainPayload marker);

	/**
	 * Write the equivalent of toString() to a buffer. Subclasses are expected to override this method.
	 * 
	 */

	public void writeToString(StringBuilder builder) {
		if(hasReferences()) {
			for(DomainPayload reference : refereces) {
				builder.append(" ");
				reference.writeToString(builder);
			}
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(1024);
		writeToString(builder);
		return builder.toString();
	}
	
	public abstract void setKey(String key, Object value);
	
	public abstract boolean definesKey(String key);
	
	public abstract void parseAndSetKey(String key, Object value);
	
	public void build(Map<String, ?> map) {
		
	}
	
    public <T extends DomainPayload> T and(DomainPayload reference) {
        add(reference);
        return (T) this;
    }
    
    public boolean hasReferences() {
        return ((refereces != null) && (refereces.size() > 0));
    }
    
    public List<DomainPayload> getRefereces() {
		return refereces;
	}
}
