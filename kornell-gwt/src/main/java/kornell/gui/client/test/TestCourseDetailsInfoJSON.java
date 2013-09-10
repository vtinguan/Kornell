package kornell.gui.client.test;

import kornell.gui.client.presentation.course.details.data.CourseDetailsTOBuilder;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class TestCourseDetailsInfoJSON extends GWTTestCase{

	@Override
	public String getModuleName() {
		return "kornell.gui.Kornell";
	}

	@Test
	public void testWidgetType() {
	    String jsonString = "{\"general\":[{\"certificationHeaderTitle\":\"Certifica\u00E7\u00E3o\",\"certificationHeaderText\":\"Confira abaixo o status dos testes e avalia\u00E7\u00F5es presentes neste curso. Seu certificado pode ser impresso por aqui caso voc\u00EA tenha conclu\u00EDdo 100% do conte\u00FAdo do curso e ter sido aprovado na avalia\u00E7\u00E3o final.\"}],\"topics\":[{\"index\":\"0\",\"title\":\"Topic1\"},{\"index\":\"1\",\"title\":\"Topic2\"},{\"index\":\"2\",\"title\":\"Topic3\"}],\"hints\":[{\"type\":\"time\",\"text\":\"Carga de estudo: 30 minutos por dia. Tempo total: 3 horas.\"},{\"type\":\"help\",\"text\":\"Se tiver d\u00FAvidas ou problemas, \u00E9 s\u00F3 clicar no \u00EDcone de ajuda no menu.\"}],\"infos\":[{\"type\":\"Apresenta\u00E7\u00E3o\",\"text\":\"Apresenta\u00E7\u00E3o...\"},{\"type\":\"Objetivos\",\"text\":\"Objetivos...\"},{\"type\":\"P\u00FAblico-alvo\",\"text\":\"P\u00FAblico-alvo...\"}],\"certifications\":[{\"type\":\"test\",\"name\":\"Pr\u00E9-teste\",\"text\":\"Esta avalia\u00E7\u00E3o tem a inten\u00E7\u00E3o de identificar o seu conhecimento referente ao tema do curso. A diferen\u00E7a da nota do pr\u00E9-teste com o p\u00F3s-teste (avalia\u00E7\u00E3o final) serve para te mostrar o ganho de conhecimento que voc\u00EA ter\u00E1 obtido ao final do curso.\"},{\"type\":\"test\",\"name\":\"P\u00F3s-teste\",\"text\":\"Esta avalia\u00E7\u00E3o final tem a inten\u00E7\u00E3o de identificar o seu conhecimento ap\u00F3s a conclus\u00E3o do curso.\"},{\"type\":\"certification\",\"name\":\"Certificado\",\"text\":\"Impress\u00E3o do certificado. Uma vez que o curso for terminado, voc\u00EA poder\u00E1 gerar o certificado aqui.\"}]}";
	    CourseDetailsTOBuilder builder = new CourseDetailsTOBuilder(jsonString);
	    assertTrue(builder.buildCourseDetails());
	}

}
