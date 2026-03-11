<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
	<head>
		<title>TBAG Game</title>

		<style>
        body {
            background-color: #2e2d30;
        }

        h1 {
            color: darkblue;
            text-align: center;
        }

        body {
            text-align: center;
            color: #00ff1e;
        }
        </style>
	</head>

	<body>
			<c:if test="${empty room}">
                <div style="font-size: 48px; ">
                Whispers of the Hollow
                </div>
            </c:if>

            <div style="font-family: courier new; font-size: 20px;">
            <table>
                <tr>
                    <td class="label">Health: ${health}</td>
                    <td class-"label">Sanity: ${sanity}</td>
                </tr>
            </table>
            </div>
			<div style="font-family: courier new; width: 500px; height: 500px; overflow: auto; border: 1px solid black; padding: 5px; text-align: left; white-space: pre-line; margin: auto;">
			${dialog}
			</div>
		<form action="${pageContext.servletContext.contextPath}/tbag" method="post">
		<div style = "margin: auto;"
			<table>
				<tr>
					<td class="label">Command:</td>
					<td><input type="text" name="command" size="50" value="${command}" /></td>
				</tr>
			</table>
			</div>
			<input name="dialog" type="hidden" value="${dialog}" />
		</form>
	</body>
</html>
