package liquibase.integration.ant;

import liquibase.Liquibase;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;

import org.apache.tools.ant.BuildException;

public class VisualizeChangelogTask extends BaseLiquibaseTask {

	Logger log = LogFactory.getLogger();
	
	@Override
	protected void executeWithLiquibaseClassloader() throws BuildException {
		
		log.info("FinavateII: VisualizeChangelogTask executed");
		
		if (!shouldRun()) {
            return;
        }
		
		Liquibase liquibase = null;
        try {
            liquibase = createLiquibase();
            liquibase.visualizeChangelog();
        } catch (Exception e) {
            throw new BuildException(e);
        } finally {
            closeDatabase(liquibase);
        }
	}

}
