/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.util.queue.ByteQueue;

public class BACnetError extends BaseType {
    private static final long serialVersionUID = -2894184233450540796L;
    private final ErrorClass errorClass;
    private final ErrorCode errorCode;

    public BACnetError(ErrorClass errorClass, ErrorCode errorCode) {
        this.errorClass = errorClass;
        this.errorCode = errorCode;
    }

    public BACnetError(BACnetServiceException e) {
        this.errorClass = e.getErrorClass();
        this.errorCode = e.getErrorCode();
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, errorClass);
        write(queue, errorCode);
    }

    public BACnetError(ByteQueue queue) throws BACnetException {
        errorClass = read(queue, ErrorClass.class);
        errorCode = read(queue, ErrorCode.class);
    }

    public ErrorClass getErrorClass() {
        return errorClass;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public boolean equals(ErrorClass errorClass, ErrorCode errorCode) {
        return this.errorClass.equals(errorClass) && this.errorCode.equals(errorCode);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((errorClass == null) ? 0 : errorClass.hashCode());
        result = PRIME * result + ((errorCode == null) ? 0 : errorCode.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "errorClass=" + errorClass + ", errorCode=" + errorCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BACnetError other = (BACnetError) obj;
        if (errorClass == null) {
            if (other.errorClass != null)
                return false;
        }
        else if (!errorClass.equals(other.errorClass))
            return false;
        if (errorCode == null) {
            if (other.errorCode != null)
                return false;
        }
        else if (!errorCode.equals(other.errorCode))
            return false;
        return true;
    }
}
