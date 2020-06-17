package com.morpheusdata.approvals

import com.morpheusdata.core.ApprovalProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.model.Instance
import com.morpheusdata.model.OptionType
import com.morpheusdata.model.Policy
import com.morpheusdata.model.Request
import com.morpheusdata.model.RequestReference
import com.morpheusdata.response.RequestResponse

class FileWatcherProvider implements ApprovalProvider {
	Plugin plugin
	MorpheusContext morpheusContext

	FileWatcherProvider(Plugin plugin, MorpheusContext context) {
		this.plugin = plugin
		this.morpheusContext = context
	}

	@Override
	MorpheusContext getMorpheusContext() {
		return morpheusContext
	}

	@Override
	Plugin getPlugin() {
		return plugin
	}

	@Override
	String getProviderCode() {
		return 'file-watcher-approval'
	}

	@Override
	String getProviderName() {
		return 'File Watcher Approval'
	}

	@Override
	RequestResponse createApprovalRequest(List instances, Request request, Policy policy, Map opts) {
		String externalRequestId = "AO_REQ_${request.id}"
		def resp
		try {
			String approvalsDirName = policy.configMap?."file-location"
			File approvalsDir = new File(approvalsDirName)
			if(!approvalsDir.exists()) {
				approvalsDir.mkdir()
			}
			File file = new File("$approvalsDirName/${externalRequestId}.txt")

			if(file.createNewFile()) {
				println "created file $file.absolutePath"
				resp = new RequestResponse(
						success: true,
						externalRequestId: externalRequestId,
						externalRequestName: 'AO Request 123',
						references: []
				)
				instances.each {
					resp.references << new RequestReference(refId: it.id, externalId: "AO_INST_$it.id", externalName: "AO Instance $it.name")
				}
				String fileContents = """requested
${resp.references*.externalId.join(',')}
"""
				file.write(fileContents)
			} else {
				println "failed to create file $file.absolutePath"
				resp = new RequestResponse(success: false)
			}
		} catch(Exception e) {
			println e.message
			resp = new RequestResponse(success: false)
		}
		resp
	}

	@Override
	List<Request> monitorApproval() {
		List approvalsResp = []
		File approvalsDir = new File('src/test/resources/approval-requests')
		approvalsDir.listFiles().each { File file ->
			println "reading file $file.absolutePath"
			List<String> lines = file.readLines()
			approvalsResp << new Request(externalId: file.name - '.txt', refs: [new RequestReference(status: RequestReference.ApprovalStatus.valueOf(lines[0]), externalId: lines[1])])
		}
		approvalsResp
	}

	@Override
	List<OptionType> policyIntegrationOptionTypes() {
		[new OptionType(name: 'Policy Option 1', fieldName: 'file-location', fieldLabel: 'File Location', displayOrder: 0)]
	}
}