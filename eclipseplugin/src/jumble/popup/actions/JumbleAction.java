package jumble.popup.actions;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class JumbleAction implements IObjectActionDelegate {
  private ICompilationUnit mCompilationUnit;

  private static final String LAUNCH_NAME = "Run Jumble";

  /**
   * Constructor for Action1.
   */
  public JumbleAction() {
    super();
  }

  /**
   * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  /**
   * @see IActionDelegate#run(IAction)
   */
  public void run(IAction action) {
    // Shell shell = new Shell();
    // MessageDialog.openInformation(
    // shell,
    // "Jumble Plug-in",
    // "Jumble Class was executed.");
    System.err.println("Selected class: " + mCompilationUnit);
    try {
      // Get launch manager
      ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
      ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

      // Delete previous configuration
      ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
      for (int i = 0; i < configurations.length; i++) {
        ILaunchConfiguration configuration = configurations[i];
        if (configuration.getName().equals(LAUNCH_NAME)) {
          configuration.delete();
          break;
        }
      }

      ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, LAUNCH_NAME);

      // Use the default JRE
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, JavaRuntime.getDefaultVMInstall().getName());
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, JavaRuntime.getDefaultVMInstall().getVMInstallType().getId());

      // Set up command line arguments
      IPackageDeclaration[] packages = mCompilationUnit.getPackageDeclarations();
      final String packageName;
      final String className;

      if (packages == null || packages.length == 0) {
        packageName = null;
      } else if (packages.length == 1) {
        packageName = packages[0].getElementName();
      } else {
        packageName = packages[0].getElementName();
        System.err.println("Error: too many packages: ");
        for (int i = 0; i < packages.length; i++) {
          System.err.println(packages[i].getElementName());
        }
      }
      final String rawClassName = mCompilationUnit.getElementName().substring(0, mCompilationUnit.getElementName().lastIndexOf('.'));
      if (packageName == null) {
        className = rawClassName;
      } else {
        className = packageName + "." + rawClassName;
      }
      System.err.println("class: " + className);

      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "jumble.Jumble");
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-r -k -i " + className);

      // Set up class path
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, mCompilationUnit.getJavaProject().getElementName());

//      List classpath = new ArrayList();
//      IJavaProject curProject = mCompilationUnit.getJavaProject();
//      IClasspathEntry[] entries = curProject.getResolvedClasspath(true);
//
//      System.err.println("Classpath: ");
//      for (int i = 0; i < entries.length; i++) {
//        System.err.println(entries[i]);
//        IRuntimeClasspathEntry entry = JavaRuntime.newRuntimeClasspathEntry();
//        classpath.add(entry.getMemento());
//      }
//      // Not sure that I am getting this path correctly
//      String pluginPath = Platform.getBundle("jumble").getLocation();
//
//      if (pluginPath.indexOf("@") >= 0) {
//        pluginPath = pluginPath.substring(pluginPath.indexOf("@") + 1);
//      }
//      System.err.println("Plugin path: " + pluginPath);
//      IRuntimeClasspathEntry jumbleEntry = JavaRuntime.newVariableRuntimeClasspathEntry(new Path(new Path(pluginPath + "jumble-runtime.jar").toFile()
//          .getAbsolutePath()));
//      classpath.add(jumbleEntry.getMemento());
//      System.err.println(classpath);
//
//      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
//      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);

      // Now run...
      ILaunchConfiguration configuration = workingCopy.doSave();
      DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @see IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
    if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
      mCompilationUnit = (ICompilationUnit) ((IStructuredSelection) selection).getFirstElement();
    }
  }

}
