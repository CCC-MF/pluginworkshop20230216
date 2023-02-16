package pluginworkshop;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class ExampleProcedureAnalyzer implements IProcedureAnalyzer {

    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IOnkostarApi onkostarApi;

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getName() {
        return "Example Procedure Analyzer";
    }

    @Override
    public String getDescription() {
        return "A simple Example Procedure Analyzer";
    }

    /**
     * This method implements a check if {@link #analyze(Procedure, Disease)} should be run for
     * a deleted {@link Procedure}
     * <p>
     * This method is deprecated in {@link IProcedureAnalyzer} and should be removed later.
     *
     * @return In this example always false
     */
    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    /**
     * This method implements a check if procedure or disease is relevant for running {@link #analyze(Procedure, Disease)}.
     * In this example {@link Procedure} must be a non {@code null} object.
     *
     * @param procedure The procedure the plugin might analyze. Can be {@code null}.
     * @param disease   The disease thie plugin might analyze. Can be {@code null}.
     * @return True if plugin handles a procedure.
     */
    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return null != procedure;
    }

    /**
     * This method gets executed each time requirements are met.
     * <ul>
     *     <li>
     *         {@link #isRelevantForDeletedProcedure()} must return 'true'.
     *         In this example no deleted procedures are processed.
     *     </li>
     *     <li>
     *         {@link #isRelevantForAnalyzer(Procedure, Disease)} must return 'true'.
     *         In this example only procedures are processed. No diseases are processed.
     *     </li>
     *     <li>
     *         {@link #getTriggerEvents()} must contain matching {@link AnalyseTriggerEvent}.
     *         In this example the trigger event must match {@code EDIT_SAVE} which will process save events after editing data.
     *     </li>
     * </ul>
     */
    @Override
    public void analyze(Procedure procedure, Disease disease) {
        logger.info("Run 'ExampleProcedureAnalyzer.analyze()'");

        var patient = procedure.getPatient();

        patient.getDiseases().forEach(patientDisease -> {
            var icd10code = patientDisease.getIcd10Code();
            // Example log! Do not use in production - personal information!
            logger.info("Found Disease {} for Patient {}", icd10code, patient.getId());

            // Do something with this data ...
        });
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.ENTRY;
    }

    /**
     * Returns set of trigger events.
     * This example will limit execution of {@link #analyze(Procedure, Disease)} to save event after editing data.
     * If not overridden, this method defaults to all {@link AnalyseTriggerEvent}s.
     *
     * @return Set of trigger events
     */
    @Override
    public Set<AnalyseTriggerEvent> getTriggerEvents() {
        return Set.of(
                AnalyseTriggerEvent.EDIT_SAVE
        );
    }
}
