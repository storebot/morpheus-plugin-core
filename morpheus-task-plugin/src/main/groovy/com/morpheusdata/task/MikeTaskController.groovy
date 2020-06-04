package com.morpheusdata.task

import com.morpheusdata.model.Permission
import com.morpheusdata.views.JsonResponse
import com.morpheusdata.views.TemplateResponse
import com.morpheusdata.views.ViewModel
import com.morpheusdata.web.PluginController
import com.morpheusdata.web.Route

class MikeTaskController implements PluginController {
	List<Route> getRoutes() {
		[
			Route.build("/mikeTask/example", "example", Permission.build("admin", "full")),
			Route.build("/mikeTask/json", "json", Permission.build("admin", "full"))
		]
	}

	def example(ViewModel<String> model) {
		println model
		return TemplateResponse.success("foo")
	}

	def json(ViewModel<Map> model) {
		println model
		model.object.foo = "fizz"
		return JsonResponse.of(model.object)
	}
}
