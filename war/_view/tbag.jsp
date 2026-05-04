<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
    <title>TBAG Game</title>

    <style>
        body {
            background-color: #2e2d30;
            text-align: center;
            color: #00ff1e;
        }

        h1 {
            color: darkblue;
            text-align: center;
        }

        /* Styles for the Interactive Hint Button */
        .npc-popup {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 250px;
            background-color: #1a1a1c;
            border: 2px solid #00ff1e;
            border-radius: 10px;
            padding: 15px;
            text-align: left;
            box-shadow: 0px 0px 15px #00ff1e;
            color: #00ff1e;
            z-index: 1000;
        }


        .hint-toggle {
            color: #ffea00;
            font-weight: bold;
            cursor: pointer;
            display: block;
        }

        .hint-toggle:hover {
            text-decoration: underline;
        }

        /* Hides the actual hint text by default */
        .hint-content {
            display: none;
            margin-top: 10px;
            border-top: 1px dashed #00ff1e;
            padding-top: 10px;
            color: #ffffff;
        }


        .stat-bar-container {
            width: 200px;
            background-color: #1a1a1c;
            border: 1px solid #00ff1e;
            margin: 0px auto;
            height: 15px;
        }

        .health-fill {
            height: 100%;
            background-color: #ff3333; /* Red for health */
            transition: width 0.5s ease-in-out;
        }

        .sanity-fill {
            height: 100%;
            background-color: #a832a8; /* Purple for sanity */
            transition: width 0.5s ease-in-out;
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
    <table style="margin: auto;">
        <tr>
            <td class="label" style="width: 210px;">Health: ${player.health}</td>
            <td class="label" style="width: 210px;">Sanity: ${player.sanity}</td>
        </tr>
    </table>

    <table style="margin: auto; margin-bottom: 15px;">
        <tr>
            <td style="width: 210px;">
                <div class="stat-bar-container">
                    <div class="health-fill" style="width: ${player.health}%;"></div>
                </div>
            </td>
            <td style="width: 210px;">
                <div class="stat-bar-container">
                    <div class="sanity-fill" style="width: ${player.sanity}%;"></div>
                </div>
            </td>
        </tr>
    </table>

    <table style="margin: auto;">
        <tr>
            <td class="label">Location: ${location}</td>
        </tr>
    </table>
</div>

<div style="font-family: courier new; width: 500px; height: 500px; overflow: auto; border: 1px solid black; padding: 5px; text-align: left; white-space: pre-line; margin: auto;">
    ${dialog}
</div>

<form action="${pageContext.servletContext.contextPath}/tbag" method="post">
    <div style="margin: auto;">
        <table>
            <tr>
                <td class="label">Command:</td>
                <td><input type="text" name="command" size="50" value="${command}" autocomplete="off"/></td>
            </tr>
        </table>
    </div>
    <input name="dialog" type="hidden" value="${dialog}" />
</form>

<c:if test="${not empty npcHint}">
    <div class="npc-popup">
                <span class="hint-toggle" onclick="document.getElementById('actual-hint').style.display = 'block';">
                    Click for Hint
                </span>

        <div id="actual-hint" class="hint-content">
                ${npcHint}
        </div>
    </div>
</c:if>
</body>
</html>