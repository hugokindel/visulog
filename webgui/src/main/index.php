<?php
$serverTimezone = "Europe/Paris";
$serverLocale = "fr_FR.UTF-8";

function init() {
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
	header("Location: "."http://".$_SERVER['HTTP_HOST']."/visulog");
}

init();

if (isset($_POST["themeChanged"])) {
	if (isset($_POST["theme"])) {
		setThemeName("light");
	} else {
		setThemeName("dark");
	}

	reload();
}
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
						<input class="link" type="text" required placeholder="Project's link">
						<div class="line"></div>
						<div class="selectPlugin">
							<div class="contentPlugin">
								<h1 style="color: white; font-size: 2.2vh; font-weight: 600;">My plugins :</h1>
								<div class="scrollPlugin">
									<!-- TODO: ajouter la liste des plugins -->

								</div>
							</div>
						</div>
						<input class="submit" type="submit" value="Start !">
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