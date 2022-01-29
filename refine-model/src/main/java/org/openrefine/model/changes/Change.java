/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.openrefine.model.changes;

import org.openrefine.history.ChangeResolver;
import org.openrefine.history.dag.DagSlice;
import org.openrefine.model.GridState;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Interface for a concrete change to a project's data.
 * 
 * There are two categories of changes: the immediate ones, which are not required to be serializable as JSON with
 * Jackson since they can be reconstructed from the operation which generated them, and the non-immediate ones (which
 * require communicating with external services for instance), which must be serializable and deserializable in JSON.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(ChangeResolver.class)
public interface Change {

    public static class DoesNotApplyException extends Exception {

        public DoesNotApplyException(String message) {
            super(message);
        }

        private static final long serialVersionUID = 1L;

    }

    /**
     * Derives the new grid state from the current grid state. Executing this method should be quick (even on large
     * datasets) since it is expected to just derive the new RDD from the existing one without actually executing any
     * expensive Spark job.
     * 
     * Long running computations should rather go in a {@class Process} generated by the operation which creates the
     * change.
     * 
     * @param projectState
     *            the state of the grid before the change
     * @return the state of the grid after the change
     * @throws DoesNotApplyException
     *             when the change cannot be applied to the given grid
     */
    public GridState apply(GridState projectState, ChangeContext context) throws DoesNotApplyException;

    /**
     * Returns true when the change is derived purely from the operation metadata and does not store any data by itself.
     * In this case it does not need serializing as it can be recreated directly by {@link Operation.createChange}.
     */
    @JsonIgnore
    public boolean isImmediate();

    /**
     * Returns a DAG slice which represents the column dependencies and outputs of this change.
     */
    @JsonProperty("dagSlice")
    public DagSlice getDagSlice();

    /*
     * @todo add ability to save optional change data
     */
}
