<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<form action="/user/register">
			<table>
				<tr>
					<td>用户名</td>
					<td><input name="userName"></td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input name="password"></td>
				</tr>
				<tr>
					<td>邮箱</td>
					<td><input name="email"></td>
				</tr>
				<tr>
					<td>电话</td>
					<td><input name="phoneNumber"></td>
				</tr>
			</table>
			<button>提交</button>
		</form>
	</div>
</body>