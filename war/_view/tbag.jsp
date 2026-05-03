<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Whispers of the Hollow</title>

    <style>
        body {
            background-color: #2e2d30;
            text-align: center;
            color: #00ff1e;
            font-family: "Courier New", monospace;
        }

        .title {
            font-size: 48px;
            margin-bottom: 20px;
            color: #00ff1e;
        }

        .game-layout {
            display: flex;
            justify-content: center;
            align-items: flex-start;
            gap: 50px;
            margin-top: 20px;
        }

        .main-panel {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .stats {
            font-size: 20px;
            margin-bottom: 15px;
        }

        .health {
            color: #ff4d4d;
            font-weight: bold;
        }

        .sanity {
            color: #66ccff;
            font-weight: bold;
        }

        .location {
            color: #ffff66;
            font-weight: bold;
        }

        .dialog-box {
            display: flex;
            flex-direction: column-reverse;
            width: 500px;
            height: 500px;
            overflow-y: auto;
            border: 1px solid black;
            padding: 8px;
            text-align: left;
            white-space: pre-line;
            margin-bottom: 15px;
            background-color: #1b1b1d;
        }

        .command-box {
            margin-top: 10px;
        }

        input[type="text"] {
            background-color: #1b1b1d;
            color: #00ff1e;
            border: 1px solid #00ff1e;
            font-family: "Courier New", monospace;
            padding: 5px;
        }

        .side-panel {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .compass {
            display: grid;
            grid-template-columns: 100px 100px 100px;
            grid-template-rows: 60px 60px 60px;
            justify-items: center;
            align-items: center;
            margin-top: 80px;
            font-size: 24px;
            font-weight: bold;
        }

        .north {
            grid-column: 2;
            grid-row: 1;
        }

        .west {
            grid-column: 1;
            grid-row: 2;
        }

        .east {
            grid-column: 3;
            grid-row: 2;
        }

        .south {
            grid-column: 2;
            grid-row: 3;
        }

        .valid {
            color: #00ff1e;
        }

        .invalid {
            color: gray;
        }

        .monster-box {
            margin-top: 25px;
            width: 320px;
            height: 190px;
            border: 2px solid #00ff1e;
            background-color: #1b1b1d;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .monster-box img {
            max-width: 290px;
            max-height: 135px;
            object-fit: contain;
        }

        .monster-name {
            margin-top: 8px;
            font-size: 20px;
            color: #ff4d4d;
            font-weight: bold;
        }

        .empty-monster-box {
            margin-top: 25px;
            width: 320px;
            height: 190px;
            border: 2px solid gray;
            background-color: #1b1b1d;
            display: flex;
            justify-content: center;
            align-items: center;
            color: gray;
            font-size: 18px;
        }
    </style>

    <script>
        window.onload = function() {
            document.getElementById("command").focus();
        };
    </script>
</head>

<body>
    <div class="title">
        Whispers of the Hollow
    </div>

    <div class="game-layout">
        <div class="main-panel">
            <div class="stats">
                <table>
                    <tr>
                        <td class="health">Health: ${player.health}</td>
                        <td class="sanity" style="padding-left: 30px;">Sanity: ${player.sanity}</td>
                    </tr>
                </table>

                <table>
                    <tr>
                        <td class="location">Location: ${location}</td>
                    </tr>
                </table>
            </div>

            <div class="dialog-box">
                ${dialog}
            </div>

            <form action="${pageContext.servletContext.contextPath}/tbag" method="post">
                <div class="command-box">
                    <table>
                        <tr>
                            <td>Command:</td>
                            <td>
                                <input type="text" id="command" name="command" size="50" autocomplete="off" />
                            </td>
                        </tr>
                    </table>
                </div>
            </form>
        </div>

        <div class="side-panel">
            <div class="compass">
                <div class="north ${canGoNorth ? 'valid' : 'invalid'}">North</div>
                <div class="west ${canGoWest ? 'valid' : 'invalid'}">West</div>
                <div class="east ${canGoEast ? 'valid' : 'invalid'}">East</div>
                <div class="south ${canGoSouth ? 'valid' : 'invalid'}">South</div>
            </div>

            <c:choose>
                <c:when test="${inCombat}">
                    <div class="monster-box">
                        <img src="${pageContext.servletContext.contextPath}/static/images/monsters/${monsterImage}"
                             alt="${monsterName}" />
                        <div class="monster-name">${monsterName}</div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="empty-monster-box">
                        No monster nearby
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>