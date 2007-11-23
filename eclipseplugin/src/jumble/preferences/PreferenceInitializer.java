package jumble.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jumble.JumblePlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = JumblePlugin.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.P_ARGS, "");
    store.setDefault(PreferenceConstants.P_VERBOSE , false);
    store.setDefault(PreferenceConstants.P_RETURNS, true);
    store.setDefault(PreferenceConstants.P_INCREMENTS, true);
    store.setDefault(PreferenceConstants.P_INLINE_CONSTANTS, true);
    store.setDefault(PreferenceConstants.P_CONSTANT_POOL_CONSTANTS, true);
    store.setDefault(PreferenceConstants.P_SWITCH, true);
  }

}
