<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script type="text/javascript">
  function validateSubmit(submit) {

    if (validateRadios() && validateCheckboxes()) {
    alert('Validazione completata con successo');  
      return false; //cambiare
    }
    alert('Non hai risposto a uno o più quesiti obbligatori');
    return false;
  }


  function validateRadios() {
    var allInputs = document.getElementsByTagName('input');
    var last = "NameUnlikelyToBeUsedAsAnElementName";

    for (i = 0; i < allInputs.length; i++) {
    	var input = allInputs[i];
    	if (input.name == last) continue;
    	
    	else if (input.type == "radio" && input.className.substring(0,13)=="requiredRadio") {
    		last = input.name;
    		var radios = document.getElementsByName(input.name);
			var radioSelected = false;
			
			// Itero sui radios 
			for (j = 0; j < radios.length; j++) {
				if (radios[j].checked) {
					radioSelected = true;
					break; // Se trova il radio checked, rompe l'iterazione e va al prossimo gruppo di radios (primo for)
				}
			}
			// Se alla fine del for precedente non ho trovato alcun radio checked
			if (!radioSelected) { // nessuna opzione selezionata
// 				alert("Non hai risposto alla domanda obbligatoria "+ input.name);
				input.focus();
				return false;
			}
    	}
    }
    return true;
  }
  

	function validateCheckboxes() {
	    var allInputs = document.getElementsByTagName('input');
	    var last = "NameUnlikelyToBeUsedAsAnElementName";

	    for (i = 0; i < allInputs.length; i++) {
	    	var input = allInputs[i];
	    	if (input.name == last) continue;
	    	
	    	else if (input.type == "checkbox" && input.className.substring(0,16)=="requiredCheckbox") {
	    		last = input.name;
	    		var checkboxes = document.getElementsByName(input.name);
				var checkboxSelected = false;
				
				// Itero sui checkbox 
				for (j = 0; j < checkboxes.length; j++) {
					if (checkboxes[j].checked) {
						checkboxSelected = true;
						break; // Se trova il checkbox checked, rompe l'iterazione e va al prossimo gruppo di checkboxes (primo for)
					}
				}
				// Se alla fine del for precedente non ho trovato alcun checkbox checked
				if (!checkboxSelected) { // nessuna opzione selezionata
// 					alert("Non hai risposto alla domanda obbligatoria "+ input.name);
					input.focus();
					return false;
				}
	    	}
	    }
		return true;
	}
</script>

${poll}
