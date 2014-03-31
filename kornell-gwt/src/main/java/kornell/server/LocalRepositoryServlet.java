package kornell.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kornell.core.util.StringUtils;

public class LocalRepositoryServlet extends HttpServlet {
	static final Pattern pattern = Pattern.compile("[/]?repository/([^/]*)/.*");
	static final Map<String, Path> repoPaths = new HashMap<String,Path>();
	static final Path home = Paths.get(System.getProperty("user.home"));
	static final Path dropbox = home.resolve("Dropbox");
	static {	
		if (dropbox.toFile().exists()){
			repoPaths.put("840e93aa-2373-4fb5-ba4a-999bb3f43888", dropbox.resolve("craftware/Clientes/MIDWAY/content"));
			repoPaths.put("42df235e-a2e8-455b-b341-84b4f8e5c88b", dropbox.resolve("craftware/Clientes/VC/content"));
			repoPaths.put("F7A4A77F-D519-4348-8F62-EDB0C2C48395", dropbox.resolve("PrismaFS-Craftware/content"));
		}
	}
	
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		String uri = req.getRequestURI();
		if(uri.startsWith("/"))
			uri = uri.substring(1);
		Matcher matcher = pattern.matcher(uri);
		if(matcher.matches()){
			String repositoryUUID = matcher.group(1);
			Path repoPath = repoPaths.get(repositoryUUID);
			if(repoPath != null){			
				Path file = repoPath.resolve(uri);
				if (file.toFile().exists()){
					setContentTye(file,req,resp);
					Files.copy(file, resp.getOutputStream());
				}else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			}else resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	static final Map<String,String> mimeTypes = new HashMap<String,String>(){{
		put(".html","text/html");
	}};
	private void setContentTye(Path file, HttpServletRequest req,
			HttpServletResponse resp) {
		String fname = file.toString();
		String ext =  fname.substring(fname.lastIndexOf("."));
		String type = mimeTypes.get(ext);
		if(StringUtils.isSome(type)){
			resp.setContentType(type);
		}
		
	}
	
}
