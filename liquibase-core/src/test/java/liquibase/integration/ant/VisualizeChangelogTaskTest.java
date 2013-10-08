package liquibase.integration.ant;

import org.apache.tools.ant.Project;
import org.junit.Test;

/**
 * Tests for {@link VisualizeChangelogTask}
 */
public class VisualizeChangelogTaskTest {

    @Test
    public void createClasspath() throws Exception {
    	VisualizeChangelogTask visualizeChangelogTask = new VisualizeChangelogTask();
    	Project project = new Project();
    	visualizeChangelogTask.setProject(project);
    	
    	visualizeChangelogTask.execute();
    }
}