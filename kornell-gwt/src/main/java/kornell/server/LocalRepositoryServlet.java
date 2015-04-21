package kornell.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kornell.core.util.StringUtils;

public class LocalRepositoryServlet extends HttpServlet {
	static final Pattern pattern = Pattern.compile("[/]?repository/([^/]*)/(.*)");
	static final String pUUID = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";

	public static final boolean isRepository(File file) {
		return file.getName().matches(pUUID);
	}

	static final Path home = Paths.get(System.getProperty("user.home"));
	static final Path repos = home.resolve("Repositories/");
	static final Map<String, File> repoPaths = new HashMap<String, File>();

	public static void scan(File file) {
		assert file.exists() && file.isDirectory();

		if (isRepository(file)) {
			System.out.println("> Adding repository [" + file+"]");
			repoPaths.put(file.getName(), file);
		}

		File[] children = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File child : children) scan(child);
	}

	@Override
	public void init() throws ServletException {
		System.out.println("*** === INIT === ***");
		scan(repos.toFile());
		System.out.println("*** === LISTEN === ***");
	}


	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		StringBuilder msg = new StringBuilder();
		try {
			String uri = req.getRequestURI();
			if (uri.startsWith("/"))
				uri = uri.substring(1);
			Matcher matcher = pattern.matcher(uri);
			if (matcher.matches()) {
				String repositoryUUID = matcher.group(1);
				msg.append("["+repositoryUUID+"] =>");
				String obj = matcher.group(2);
				Path repoPath = pathOf(repositoryUUID);				
				msg.append("["+repoPath+"]");				
				if (repoPath != null) {					
					if (obj.startsWith("/")){
						obj=obj.substring(1);
					}
					msg.append("["+obj+"]");
					Path file = repoPath.resolve(obj);					
					msg.append("=>["+file+"]");
					if (file.toFile().exists()) {
						msg.append("[W]");
						setContentTye(file, req, resp);
						Files.copy(file, resp.getOutputStream());
					} else {
						msg.append("[NO_FILE]["+file.toAbsolutePath()+"]");
						throw new RuntimeException("# "+msg);
					}
				} else { 
					msg.append("[NO_REPO]");
					throw new RuntimeException("# "+msg);
				}
			} else {
				msg.append("[NOT_MINE]");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"500>"+e.getMessage());
		}finally{
			System.out.println(">"+msg);
		}
	}

	static final Path pathOf(String storeUUID) {
		File base = repoPaths.get(storeUUID);
		return base != null ? Paths.get(base.toURI()) : null;
	}

	@SuppressWarnings("all")
	static final Map<String, String> mimeTypes = new HashMap<String, String>() {
		{
			put(".txt", "text/plain");
			put(".html", "text/html");
			put(".js", "application/javascript");
		}
	};

	private void setContentTye(Path file, HttpServletRequest req,
			HttpServletResponse resp) {
		String fname = file.toString();
		String ext = fname.substring(fname.lastIndexOf("."));
		String type = mimeTypes.get(ext);
		if (StringUtils.isSome(type)) {
			resp.setContentType(type);
		}

	}

}
