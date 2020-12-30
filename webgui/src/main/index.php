<?php
$serverTimezone = "Europe/Paris";
$serverLocale = "fr_FR.UTF-8";
$listPlugins = null;
$fileCreated = "";
$path = "";
$selectedPlugins = null;

function start() {
	global $serverTimezone;
	global $serverLocale;

	date_default_timezone_set($serverTimezone);
	setlocale(LC_TIME, $serverLocale);

	if (!isset($_SESSION)) {
		session_start();
		session_regenerate_id();
	}

	if (!isset($_COOKIE["theme"])) {
		setThemeName("light");
	}
}

function getThemeName() {
	return isset($_COOKIE["theme"]) ? $_COOKIE["theme"] : "light";
}

function setThemeName($name) {
	setcookie('theme', $name, time() + 365243600, "/", null, false, true);
}

function reload() {
	header("Location: ".(isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]");
}

function findListOfPlugins() {
	global $listPlugins;

	exec("cli --list-plugins", $listPluginsCommand, $retval);
	$listPlugins = explode(",", $listPluginsCommand[0]);
}

function startsWith( $haystack, $needle ) {
     $length = strlen( $needle );
     return substr( $haystack, 0, $length ) === $needle;
}

function endsWith( $haystack, $needle ) {
    $length = strlen( $needle );
    if( !$length ) {
        return true;
    }
    return substr( $haystack, -$length ) === $needle;
}

start();

if (isset($_POST["themeChanged"])) {
	if (isset($_POST["theme"])) {
		setThemeName("light");
	} else {
		setThemeName("dark");
	}

	reload();
} else if (isset($_POST["resultAsked"])) {
	if (count($_POST["plugins"]) > 0) {
		$path = $_POST["path"];
		$path = str_replace("\\", "/", $path);
		$plugins = "";

		for ($i = 0; $i < count($_POST["plugins"]); $i++) {
			$plugins = $plugins.$_POST["plugins"][$i].($i < count($_POST["plugins"]) - 1 ? "," : "");
		}

		$selectedPlugins = $plugins;

		exec("cli $path --plugins=$plugins --get-result", $output, $retval);
		$fileCreated = $output[0];
	}
}

findListOfPlugins();
?>

<!DOCTYPE html>
<html lang="fr">
<head>
	<meta charset="UTF-8">
	<meta name="viewport"
		  content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="ie=edge">
	<link rel="icon" href="assets/images/visulog.png">
	<link rel="stylesheet" href="css/normalize.css">
	<link rel="stylesheet" href="css/index.css">
	<link rel="stylesheet" href="css/<?php echo getThemeName(); ?>.css">
	<title>visulog</title>
</head>
<body>
	<?php if(!empty($fileCreated)) { ?>
	    <script type="text/javascript">
	        window.open("<?php echo $fileCreated; ?>", '_blank');
	        window.location.href = window.location.protocol + '//' + 
	       		window.location.host + window.location.pathname +
	       		"?path=" + encodeURI("<?php echo $path; ?>") +
	       		"&plugins=" + encodeURI("<?php echo $selectedPlugins; ?>");
	    </script>
	<?php  } ?>

	<div class="root">
		<div class="top">
			<div class="topContent">
				<a href="https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/visulog" target="_blank"><img src="assets/images/gitlab.png" alt="" style="height: 6vh"></a>
				<span>VISULOG</span>
				<form id="theme-form" action="" method="post">
					<div class="toggle">
						<input name="themeChanged" type="hidden" value="1">
						<span style="margin-right: 3px;">🌙</span>
						<input type="checkbox" name="theme" value="1" onclick="document.getElementById('theme-form').submit();" id="toggle-switch" <?php echo (getThemeName() == "light") ? "checked" : ""; ?>/>
						<label for="toggle-switch"></label>
						<span style="margin-left: 3px;">☀️</span>
					</div>
				</form>
			</div>
		</div>
		<div class="content">
			<div>
				<div class="form">
					<form id="main-form" action="" method="post">
						<input name="resultAsked" type="hidden" value="1">
						<input class="link" type="text" name="path" required placeholder="Chemin d'accès de dépôt" value="<?php echo (isset($_GET['path']) ? $_GET['path'] : '');?>">
						<div class="line"></div>
						<div class="selectPlugin">
							<div class="contentPlugin">
								<h1 style="color: white; font-size: 2.2vh; font-weight: 600;">Plugins disponibles :</h1>
								<div class="scrollPlugin">
									<?php
									for($i = 0; $i < count($listPlugins); $i++) {
										$check = isset($_GET["plugins"]) && (strpos($_GET["plugins"], $listPlugins[$i].",") !== false || endsWith($_GET["plugins"], $listPlugins[$i]));

										echo "<input type='checkbox' id='checkbox$listPlugins[$i]' name='plugins[]' value='$listPlugins[$i]' ".($check ? "checked=\"\"" : "").">";
										echo "<label for='checkbox$listPlugins[$i]'>$listPlugins[$i]</label>";
										echo "<br>";
									}
									?>
								</div>
							</div>
						</div>
						<input class="submit" type="submit" value="Obtenir le résultat">
					</form>
				</div>
				<div class="about">
					<h1 style="margin-bottom: 3vh; font-size: 4vh;">À quoi sert visulog ?</h1>
					<p style="font-weight: 500; font-size: 2vh;">visulog est un outils d'analyse git programmé en Java dans un environnement de travail type Scrum dans un but éducatif, son intérêt principal est l'analyse de dépôt git, il peux être utiliser à la main en ligne de commande (et peux même pouvoir être utilisé dans un environnement automatisé grâce à ses différentes commandes) ou bien à l'aide de cette interface graphique. À l'aide de différents plugins pouvant être choisi par l'utilisateur il permet d'obtenir un résultat sous forme de page HTML pouvant être lu sur n'importe quel navigateur de manière à obtenir les résultats d'analyse voulu sous forme clair et lisible. Les plugins permettent d'obtenir des informations sur les commits, branches, auteur et l'état général d'un dépôt git. Son développement est réalisé par 7 étudiants de l'Université de Paris.</p>
				</div>
			</div>
			<img src="assets/images/monitor.png" alt="">
		</div>
	</div>

	<script>
		// Function to force CSS reload (for debugging purposes).
		(function() {
			var h;
			var f;
			var a = document.getElementsByTagName('link');

			for (h = 0; h < a.length; h++) {
				f = a[h];
				if (f.rel.toLowerCase().match(/stylesheet/) && f.href) {
					var g = f.href.replace(/(&|\?)rnd=\d+/, '');
					f.href = g + (g.match(/\?/) ? '&' : '?');
					f.href += 'rnd=' + (new Date().valueOf());
				}
			}
		})();
	</script>
</body>
</html>