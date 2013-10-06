<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script type="text/javascript">
	function validateSubmit(submit) {

		var radios = document.getElementsByClassName("requiredRadio");

		if (validateRadio(radios)) {

			if (validateCheckboxes()) {
				return true;
			} else {
				alert('Non hai risposto a uno o più quesiti obbligatori');
				return false;
			}
		} else {
			alert('Non hai risposto a uno o più quesiti obbligatori');
			return false;
		}
	}

	function validateRadio(inputs) {

		// 	  alert("validateRadios");

		for (i = 0; i < inputs.length; i++) {

			if (inputs[i].checked) {
				//     	  alert("validateRadios inputchecked");
				return true;
			}
		}
		return false;
	}

	function validateCheckboxes() {
		// 	  alert("validateCheckboxes");
		var inputs = document.getElementsByTagName('input');
		var checkboxes = [];

		// prendo tutte le checkbox 
		for (i = 0; i < inputs.length; i++) {
			if (inputs[i].type.toLowerCase() == 'checkbox') {
				checkboxes.push(inputs[i]);
				//         alert("sono qui");
			}
		}
		//     alert("validateCheckboxes dopo il push");
		if (checkboxes.length < 1) {
			return true;
		}

		// prendo tutti gli elementi che hanno il classname della checkbox (ossia tutte le checkbox collegate tra loro) 
		for (i = 0; i < checkboxes.length; i++) {
			checkboxClassName = checkboxes[i].className;
			linkedCheckboxes = document
					.getElementsByClassName(checkboxClassName);
			for (i = 0; i < linkedCheckboxes.length; i++) {
				if (linkedCheckboxes[i].checked) {
					return true;
					//         } else {
					//           return false; SBAGLIATO: Se lo decommentassi otterrei l'alert ogni volta che non venisse selezionata la prima checkbox. 
				}
			}
		}
	}
</script>

${poll}
