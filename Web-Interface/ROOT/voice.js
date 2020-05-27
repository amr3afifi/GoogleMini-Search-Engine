var message = document.querySelector('#message');

        var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition;
        var SpeechGrammarList = SpeechGrammarList || webkitSpeechGrammarList;

        var grammar = '#JSGF V1.0;'

        var recognition = new SpeechRecognition();
        var speechRecognitionList = new SpeechGrammarList();
        speechRecognitionList.addFromString(grammar, 1);
        recognition.grammars = speechRecognitionList;
        recognition.lang = 'en-US';
        recognition.interimResults = false;

        recognition.onresult = function(event) {
            var last = event.results.length - 1;
            var command = event.results[last][0].transcript;

            document.getElementById("searchBox").value = command.toLowerCase();
        };

        recognition.onspeechend = function() {
            recognition.stop();
            document.getElementById("voiceSearch").src = "./mic.png";
        };

        recognition.onerror = function(event) {
            message.textContent = 'Error occurred in recognition: ' + event.error;
            document.getElementById("voiceSearch").src = "./mic.png";
        }        

        document.querySelector('#voiceSearch').addEventListener('click', function(){
            document.getElementById("searchBox").value = "";
            message.textContent = '';
            recognition.start();
            document.getElementById("voiceSearch").src = "./dots.png";

        });

        document.querySelector('#searchBox').addEventListener('click', function(){
            message.textContent = '';
        });

