package com.morpheusdata.core;

import com.morpheusdata.model.*;
import io.reactivex.Single;

import java.util.Collection;
import java.util.Map;

/**
 * Provides a means to interact or query data from the main Morpheus application back into the various provider extensions
 * It is important to note that most methods in the context are asynchronous and rely on RxJava based interfaces so as
 * to present the ability for the implementation of the MorpheusContext to be disconnected from the core application.
 *
 * The MorpheusContext typically provides getters for multiple subcontexts. These Sub Context interfaces are useful for
 * organizing calls so as to reduce the size of the individual Context Class
 *
 * (i.e. a Connector app could implement the MorpheusContext and relay communication back to the Morpheus Application itself)
 *
 * @see MorpheusComputeContext
 * @see MorpheusNetworkContext
 * @see MorpheusTaskContext
 *
 * @author David Estes
 */
public interface MorpheusContext {

	/**
	 * Returns the Compute Context used for performing updates or queries on compute related assets within Morpheus
	 * @return An Instance of the Compute Context to be used typically by {@link CloudProvider} implementations.
	 */
	MorpheusComputeContext getCompute();


	/**
	 * Returns the NetworkContext used for performing updates or queries on network related assets within Morpheus.
	 * Typically this would be called by a {@link DNSProvider} or {@link IPAMProvider}.
	 * @return An Instance of the Network Context to be used for calls by various network providers
	 */
	MorpheusNetworkContext getNetwork();

	/**
	 * Returns the Task context used for automation tasks on assets within Morpheus.
	 * Typically this would be called by a {@link TaskProvider}.
	 * @return An Instance of the Task Context to be used for calls by various task providers
	 */
	MorpheusTaskContext getTask();



	//Common methods used across various contexts

	/**
	 * Execute an ssh command
	 * @param address internet address
	 * @param port port number
	 * @param username ssh username
	 * @param password ssh password
	 * @param command the command to be executed
	 * @param publicKey public key as a String
	 * @param privateKey private key as a string
	 * @param passPhrase passphrase for <code>privateKey</code>
	 * @param ignoreExitStatus defaults to false. When enabled, marks the command execution as successful, regardless of exit code
	 * @param logLevel defaults to {@link LogLevel} debug
	 * @param doPty Allocate a Pseudo-Terminal
	 * @param runAsUser specify a user to run the command as
	 * @param sudo execute the command with sudo permissions
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeSshCommand(String address, Integer port, String username, String password, String command, String publicKey, String privateKey, String passPhrase, Boolean ignoreExitStatus, LogLevel logLevel, Boolean doPty, String runAsUser, Boolean sudo);

	/**
	 * Execute a command on a Windows machine
	 *
	 * @param address internet address
	 * @param port port number
	 * @param username connection username
	 * @param password connection password
	 * @param command the command to be executed
	 * @param noProfile add a <code>–noprofile</code> argument to PowerShell
	 * @param elevated use elevated privileges
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeWindowsCommand(String address, Integer port, String username, String password, String command, Boolean noProfile, Boolean elevated);


	/**
	 * Execute a command on a Container or VM using the standard connection details
	 *
	 * @param container resource to execute on which to execute the command
	 * @param command the command to be executed
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeCommandOnWorkload(Container container, String command);

	/**
	 * Execute a command on a Container or VM using custom connection details
	 *
	 * @param container resource to execute on which to execute the command
	 * @param command the command to be executed
	 * @param sshUsername username
	 * @param sshPassword password
	 * @param publicKey public key as a String
	 * @param privateKey private key as a String
	 * @param passPhrase passphrase for <code>privateKey</code>
	 * @param noProfile for Windows VMs, add a <code>–noprofile</code> argument to PowerShell
	 * @param runAsUser run the command as a specific user
	 * @param sudo execute the command with sudo permissions
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeCommandOnWorkload(Container container, String command, String sshUsername, String sshPassword, String publicKey, String privateKey, String passPhrase, Boolean noProfile, String runAsUser, Boolean sudo);

	/**
	 *  Execute a command on a server using the default connection details
	 *
	 * @param server server on which to execute the command
	 * @param command the command to be executed
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeCommandOnServer(ComputeServer server, String command);

	/**
	 * Execute a command on a server using custom connection details
	 *
	 * @param server server on which to execute the command
	 * @param command the command to be executed
	 * @param rpc when enabled, override the agent mode and execute over ssh/winrm
	 * @param sshUsername username
	 * @param sshPassword password
	 * @param publicKey public key as a String
	 * @param privateKey private key as a String
	 * @param passPhrase passphrase for <code>privateKey</code>
	 * @param noProfile for Windows VMs, add a <code>–noprofile</code> argument to PowerShell
	 * @param sudo execute the command with sudo permissions
	 * @return A result object detailing the command execution
	 */
	Single<TaskResult> executeCommandOnServer(ComputeServer server, String command, Boolean rpc, String sshUsername, String sshPassword, String publicKey, String privateKey, String passPhrase, Boolean noProfile, Boolean sudo);

	Single<TaskConfig> buildInstanceConfig(Instance instance, Map baseConfig, Task task, Collection excludes, Map opts);
	Single<TaskConfig> buildContainerConfig(Container container, Map baseConfig, Task task, Collection excludes, Map opts);
	Single<TaskConfig> buildComputeServerConfig(ComputeServer container, Map baseConfig, Task task, Collection excludes, Map opts);

	//TODO: Add Locking Provider RPC Calls to acquire distributed locks when necessary

}
