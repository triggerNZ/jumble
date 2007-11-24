package jumble.popup.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jumble.JumblePlugin;
import jumble.preferences.PreferenceConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class JumbleAction implements IObjectActionDelegate, IEditorActionDelegate {
  private static final String PATH_SEPARATOR = System.getProperty("path.separator");

  private ICompilationUnit mCompilationUnit;

  private IFile mFile;

  private IEditorPart mTargetEditor;

  private static final String LAUNCH_NAME = "Run Jumble";

  /**
   * Constructor for Action1.
   */
  public JumbleAction() {
    super();
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    mTargetEditor = targetEditor;
    mFile = null;
    mCompilationUnit = null;
  }

  /**
   * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  /**
   * Does the actual jumbling part.
   * 
   * @see IActionDelegate#run(IAction)
   */
  public void run(IAction action) {
    final IPreferenceStore prefs = JumblePlugin.getDefault().getPreferenceStore();
    final String pluginLocation;
    try {
      pluginLocation = JumblePlugin.getDefault().getPluginFolder().getAbsolutePath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

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
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, JavaRuntime.getDefaultVMInstall().getInstallLocation()
          .toString());

      // Use the specified JVM arguments
      String extraArgs = prefs.getString(PreferenceConstants.P_ARGS);

      // Set up command line arguments
      String className = getClassName();

      // Set up class paths
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
      List<String> classpath = new ArrayList<String>();
      IPath jumbleJarPath = new Path(pluginLocation + "/jumble.jar");
      IRuntimeClasspathEntry jumbleJarEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(jumbleJarPath);
      classpath.add(jumbleJarEntry.getMemento());
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
      IJavaProject curProject = getJavaProject();
      String classPath = getClasspath(curProject);

      boolean verbose = prefs.getBoolean(PreferenceConstants.P_VERBOSE);
      boolean returnVals = prefs.getBoolean(PreferenceConstants.P_RETURNS);
      boolean increments = prefs.getBoolean(PreferenceConstants.P_INCREMENTS);
      boolean inlineConstants = prefs.getBoolean(PreferenceConstants.P_INLINE_CONSTANTS);
      boolean constantPoolConstants = prefs.getBoolean(PreferenceConstants.P_CONSTANT_POOL_CONSTANTS);
      boolean switchStatements = prefs.getBoolean(PreferenceConstants.P_SWITCH);
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "com.reeltwo.jumble.Jumble");
      workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (returnVals ? "-r " : "") + (inlineConstants ? "-k " : "")
          + (increments ? "-i " : "") + (verbose ? "-v " : "") + (constantPoolConstants ? "-w " : "") + (switchStatements ? "-j " : "")
          + "--classpath \"" + classPath + "\" " + " " + extraArgs + " " + className);

      // Now run...
      ILaunchConfiguration configuration = workingCopy.doSave();
      System.err.println("Launching...");
      DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getClasspath(IJavaProject curProject) throws JavaModelException {
    IClasspathEntry[] entries = curProject.getResolvedClasspath(true);
    StringBuffer cpBuffer = new StringBuffer();

    IWorkspaceRoot root = curProject.getProject().getWorkspace().getRoot();
    IPath outputLocation = root.findMember(curProject.getOutputLocation()).getLocation();
    for (IClasspathEntry entry : entries) {
      IPath path = entry.getPath();

      IResource res = root.findMember(path);

      final String curPath;
      if (res == null) {
        curPath = path.toOSString();
      } else {
        if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
          curPath = outputLocation.toOSString();
        } else if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT){
          curPath = getClasspath(JavaCore.create((IProject) res));
        } else {
          curPath = res.getLocation().toOSString();
        }
      }

      if (cpBuffer.length() == 0) {
        cpBuffer.append(curPath);
      } else {
        cpBuffer.append(PATH_SEPARATOR + curPath);
      }
    }
    return cpBuffer.toString();
  }

  /**
   * @see IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
    if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
      Object selectedItem = ((IStructuredSelection) selection).getFirstElement();

      if (selectedItem instanceof ICompilationUnit) {
        mCompilationUnit = (ICompilationUnit) selectedItem;
        mFile = null;
      } else if (selectedItem instanceof IFile) {
        mFile = (IFile) selectedItem;
        mCompilationUnit = null;
      }
    }
  }

  private String getClassName() throws JavaModelException {
    ICompilationUnit cu = getCompilationUnit();

    IPackageDeclaration[] packages = cu.getPackageDeclarations();
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
    final String rawClassName = cu.getElementName().substring(0, cu.getElementName().lastIndexOf('.'));
    if (packageName == null) {
      className = rawClassName;
    } else {
      className = packageName + "." + rawClassName;
    }
    return className;
  }

  private IJavaProject getJavaProject() {
    return getCompilationUnit().getJavaProject();
  }

  private ICompilationUnit getCompilationUnit() {
    if (mTargetEditor == null) {
      ICompilationUnit cu = mCompilationUnit;

      if (cu == null) {
        cu = JavaCore.createCompilationUnitFrom(mFile);
      }
      return cu;
    }

    IFileEditorInput f = (IFileEditorInput) mTargetEditor.getEditorInput();
    System.err.println(f.getFile());
    return JavaCore.createCompilationUnitFrom(f.getFile());
  }
}
