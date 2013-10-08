package liquibase.integration.ant;

import java.io.Writer;

import liquibase.Liquibase;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.util.ui.UIFactory;

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

            if (isPromptOnNonLocalDatabase()
                    && !liquibase.isSafeToRunUpdate()
                    && UIFactory.getInstance().getFacade().promptForNonLocalDatabase(liquibase.getDatabase())) {
                throw new BuildException("Chose not to run against non-production database");
            }

            Writer writer = createOutputWriter();
            if (writer == null) {
                liquibase.visualize(getContexts());
            } else {
            	liquibase.visualize(getContexts(), writer);
                writer.flush();
                writer.close();
            }

        } catch (Exception e) {
            throw new BuildException(e);
        } finally {
            closeDatabase(liquibase);
        }
	}

}
