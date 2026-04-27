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
        /*Styles for the Interactive Hint Button */
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


        /* Makes it look clickable! */
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
            <td class="label">Health: ${player.health}</td>
            <td class-"label">Sanity: ${player.sanity}</td>
        </tr>
    </table>
    <table>
        <tr>
            <td class="label">Location: ${location}</td>
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
<c:if test="${not empty npcHint}">
    <div class="npc-popup">
               <span class="hint-toggle" onclick="document.getElementById('actual-hint').style.display = 'block';">
                   💡 Click for Hint
               </span>


        <div id="actual-hint" class="hint-content">
                ${npcHint}
        </div>
    </div>
</c:if>
</body>
</html>
