import 'package:flutter/material.dart';


class HomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
			appBar: AppBar(
				backgroundColor: Colors.grey,
				title: Text("首页"),
			),
			body: Center(
				child: Text("首页"),
			),
		);
  }
}
