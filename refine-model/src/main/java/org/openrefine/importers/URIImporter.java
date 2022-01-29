
package org.openrefine.importers;

import org.openrefine.ProjectMetadata;
import org.openrefine.importing.ImportingJob;
import org.openrefine.model.DatamodelRunner;
import org.openrefine.model.GridState;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A base class for importers which read files specified by a URI
 * 
 * @author Antonin Delpeuch
 *
 */
public abstract class URIImporter extends ImportingParserBase {

    protected URIImporter(DatamodelRunner runner) {
        super(runner);
    }

    /**
     * Parses one file, designated by an URI (for instance for direct loading by Spark, Flink…).
     * 
     * @param metadata
     *            the project metadata associated with the project to parse (which can be modified by the importer)
     * @param job
     *            the importing job where this import is being done
     * @param fileSource
     *            the original path or source of the file (could be "clipboard" or a URL as well)
     * @param archiveFileName
     *            the original name of the archive the file was extracted from
     * @param uri
     *            the HDFS URI where to read the data from
     * @param limit
     *            the maximum number of rows to read
     * @param options
     *            any options passed to the importer as a JSON payload
     * @param progress
     *            a callback for tracking the progress of reading this file
     * @return a parsed GridState
     */
    public abstract GridState parseOneFile(ProjectMetadata metadata, ImportingJob job, String fileSource,
            String archiveFileName, String uri, long limit, ObjectNode options, MultiFileReadingProgress progress) throws Exception;

}
