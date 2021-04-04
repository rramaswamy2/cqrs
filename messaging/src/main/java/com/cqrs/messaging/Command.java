package com.cqrs.messaging;

import java.util.Objects;

public abstract class Command implements Action {
    private int originalVersion;

    protected void setOriginalVersion(int originalVersion) {
        this.originalVersion = originalVersion;
    }

    public int getOriginalVersion() {
        return originalVersion;
    }

	@Override
	public int hashCode() {
		return Objects.hash(originalVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Command other = (Command) obj;
		return originalVersion == other.originalVersion;
	}
    
}
