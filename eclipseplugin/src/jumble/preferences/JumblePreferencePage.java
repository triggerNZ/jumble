package jumble.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import jumble.JumblePlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class JumblePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public JumblePreferencePage() {
    super(GRID);
    setPreferenceStore(JumblePlugin.getDefault().getPreferenceStore());
    setDescription("Jumble Preferences");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI
   * blocks needed to manipulate various types of preferences. Each field editor
   * knows how to save and restore itself.
   */
  @Override
  public void createFieldEditors() {
    addField(new BooleanFieldEditor(PreferenceConstants.P_CONSTANT_POOL_CONSTANTS, "Mutate Constant &Pool Constants", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.P_INLINE_CONSTANTS, "Mutate Inline &Constants", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.P_INCREMENTS, "Mutate &Increment Instructions", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.P_RETURNS, "Mutate &Return Values", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.P_SWITCH, "Mutate &Switch Statements", getFieldEditorParent()));
    
    addField(new BooleanFieldEditor(PreferenceConstants.P_VERBOSE, "&Verbose Mode", getFieldEditorParent()));
    
    addField(new StringFieldEditor(PreferenceConstants.P_ARGS, "Jumble &Arguments", getFieldEditorParent()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench) {
  }

}