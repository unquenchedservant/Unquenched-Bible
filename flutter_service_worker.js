'use strict';
const MANIFEST = 'flutter-app-manifest';
const TEMP = 'flutter-temp-cache';
const CACHE_NAME = 'flutter-app-cache';

const RESOURCES = {"canvaskit/canvaskit.js": "140ccb7d34d0a55065fbd422b843add6",
"canvaskit/canvaskit.js.symbols": "58832fbed59e00d2190aa295c4d70360",
"canvaskit/canvaskit.wasm": "07b9f5853202304d3b0749d9306573cc",
"canvaskit/chromium/canvaskit.js": "5e27aae346eee469027c80af0751d53d",
"canvaskit/chromium/canvaskit.js.symbols": "193deaca1a1424049326d4a91ad1d88d",
"canvaskit/chromium/canvaskit.wasm": "24c77e750a7fa6d474198905249ff506",
"canvaskit/skwasm.js": "1ef3ea3a0fec4569e5d531da25f34095",
"canvaskit/skwasm.js.symbols": "0088242d10d7e7d6d2649d1fe1bda7c1",
"canvaskit/skwasm.wasm": "264db41426307cfc7fa44b95a7772109",
"canvaskit/skwasm_heavy.js": "413f5b2b2d9345f37de148e2544f584f",
"canvaskit/skwasm_heavy.js.symbols": "3c01ec03b5de6d62c34e17014d1decd3",
"canvaskit/skwasm_heavy.wasm": "8034ad26ba2485dab2fd49bdd786837b",
"flutter.js": "888483df48293866f9f41d3d9274a779",
"flutter_bootstrap.js": "35acd240df37f517cbeb37a01277a0ed",
"index.html": "da8b835a191e09e9702c1afa373316dd",
"/": "da8b835a191e09e9702c1afa373316dd",
"main.dart.js": "04852d39eee5242339173025cb2555a4",
"version.json": "54cec5660798dd48a756b9c5a0903e6d",
"assets/packages/material_design_icons_flutter/lib/fonts/materialdesignicons-webfont.ttf": "d10ac4ee5ebe8c8fff90505150ba2a76",
"assets/packages/cupertino_icons/assets/CupertinoIcons.ttf": "33b7d9392238c04c131b6ce224e13711",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Free-Regular-400.otf": "b2703f18eee8303425a5342dba6958db",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Free-Solid-900.otf": "ae2652c858cb737e2d6cddc0c3091132",
"assets/packages/font_awesome_flutter/lib/fonts/Font-Awesome-7-Brands-Regular-400.otf": "c33274f44c7d8dbab894e8e36ee05165",
"assets/packages/sign_in_button/assets/logos/google_light.png": "f71e2d0b0a2bc7d1d8ab757194a02cac",
"assets/packages/sign_in_button/assets/logos/3.0x/google_light.png": "3aeb09c8261211cfc16ac080a555c43c",
"assets/packages/sign_in_button/assets/logos/3.0x/google_dark.png": "c75b35db06cb33eb7c52af696026d299",
"assets/packages/sign_in_button/assets/logos/3.0x/facebook_new.png": "689ce8e0056bb542425547325ce690ba",
"assets/packages/sign_in_button/assets/logos/2.0x/google_light.png": "1f00e2bbc0c16b9e956bafeddebe7bf2",
"assets/packages/sign_in_button/assets/logos/2.0x/google_dark.png": "68d675bc88e8b2a9079fdfb632a974aa",
"assets/packages/sign_in_button/assets/logos/2.0x/facebook_new.png": "dd8e500c6d946b0f7c24eb8b94b1ea8c",
"assets/packages/sign_in_button/assets/logos/google_dark.png": "d18b748c2edbc5c4e3bc221a1ec64438",
"assets/packages/sign_in_button/assets/logos/facebook_new.png": "93cb650d10a738a579b093556d4341be",
"assets/fonts/MaterialIcons-Regular.otf": "6ddd73a7edd044845a24aa6a531c9f29",
"assets/shaders/ink_sparkle.frag": "ecc85a2e95f5e9f53123dcaf8cb9b6ce",
"assets/AssetManifest.json": "eaa03662de065e56ffeb3f8c41223e2d",
"assets/AssetManifest.bin": "55eefc44cfc9bd7f2e44c3eca0f4e413",
"assets/AssetManifest.bin.json": "97c335c72231e8ba288def4f1fc56239",
"assets/FontManifest.json": "e9142eb1cc01949e09b4ef7bec93be73",
"assets/NOTICES": "29f2cc3c709d93195c49428f13a99aba",
"icons/Icon-192.png": "ac9a721a12bbc803b44f645561ecb1e1",
"icons/Icon-512.png": "96e752610906ba2a93c65f8abe1645f1",
"icons/Icon-maskable-192.png": "c457ef57daa1d16f64b27b786ec2ea3c",
"icons/Icon-maskable-512.png": "301a7604d45b3e739efc881eb04896ea",
"favicon.png": "5dcef449791fa27946b3d35ad8803796",
"manifest.json": "076ca029d90718273e36392d3d706375",
"privacy_policy.html": "94ed0e95f1511b6d642016d257ccb58d",
"404.html": "d86e02b4b8b59fc21162c245a24b5add",
".git/hooks/applypatch-msg.sample": "ce562e08d8098926a3862fc6e7905199",
".git/hooks/commit-msg.sample": "579a3c1e12a1e74a98169175fb913012",
".git/hooks/fsmonitor-watchman.sample": "a0b2633a2c8e97501610bd3f73da66fc",
".git/hooks/post-update.sample": "2b7ea5cee3c49ff53d41e00785eb974c",
".git/hooks/pre-applypatch.sample": "054f9ffb8bfe04a599751cc757226dda",
".git/hooks/pre-commit.sample": "5029bfab85b1c39281aa9697379ea444",
".git/hooks/pre-merge-commit.sample": "39cb268e2a85d436b9eb6f47614c3cbc",
".git/hooks/pre-push.sample": "2c642152299a94e05ea26eae11993b13",
".git/hooks/pre-rebase.sample": "56e45f2bcbc8226d2b4200f7c46371bf",
".git/hooks/pre-receive.sample": "2ad18ec82c20af7b5926ed9cea6aeedd",
".git/hooks/prepare-commit-msg.sample": "2b5c047bdb474555e1787db32b2d2fc5",
".git/hooks/push-to-checkout.sample": "c7ab00c7784efeadad3ae9b228d4b4db",
".git/hooks/sendemail-validate.sample": "4d67df3a8d5c98cb8565c07e42be0b04",
".git/hooks/update.sample": "647ae13c682f7827c22f5fc08a03674e",
".git/info/exclude": "036208b4a1ab4a235d75c181e685e5a3",
".git/description": "a0a7c3fff21f2aea3cfa1d0316dd816c",
".git/refs/heads/main": "8b2ef3e38d37f905b7219cc05976325d",
".git/refs/remotes/origin/HEAD": "98b16e0b650190870f1b40bc8f4aec4e",
".git/refs/remotes/origin/main": "8b2ef3e38d37f905b7219cc05976325d",
".git/objects/76/e0cfde241f59ba59948af10fb5829e699c2d1b": "b785c82892434320d8f93d66c37cc886",
".git/objects/04/daf17ef20d35c8fcf23e943e18d85700169619": "76fc622e316c8c92ae054886954d0ff8",
".git/objects/36/0211c1a01783492308b17b676e49b594dc48d9": "5480560e84dbc5baa2e186bf8e947622",
".git/objects/49/16a1c0fd386ca8e1d79e6b72c762db67115b7a": "dedf4c3923ae80d039835805be31d2b8",
".git/objects/49/ea6a7a0dffabf4e2b4074f252b3e9bdf95968b": "9b7b983ab6632b29d44baa81da59d59b",
".git/objects/9a/362c1a4a062fc7da4239c4d921e474ca64bef5": "643f57d1a268a7b53c4ccfc8cb189512",
".git/objects/b0/5402a4222842b341d200f754de66e51434a833": "7974d934df9700ca5fc20f7fd143ea16",
".git/objects/b0/0c684d3ef14be87f0badd2eecc88babc70fea0": "62b179c5e55028c8088598f00ea6c781",
".git/objects/b0/a9fb7c4f7d7eae2bba2b82b8d7183dcc272de6": "a4d0ae878802a6e3c9e36aaa0a9b1eca",
".git/objects/c2/08b672bf6dd68015685f6c7a989da090c6e7e7": "a22c27d253def52e523c9e86dc856238",
".git/objects/6a/d24ec619b50ef72445007ca436e7d73fff216d": "26cc75b0749fd7c99a98b2aa34b913b7",
".git/objects/e9/94225c71c957162e2dcc06abe8295e482f93a2": "70d7e891b9a9a44b4a65cd9990250a20",
".git/objects/e9/4fcb3a9590f58eec55f6ee24c62a846abbe337": "9b6ffd7090d6e4478ea3619b14b812bc",
".git/objects/e9/2aa835e59016f2dda51a3f458c424be305c669": "e8d00c90e4fd6f26c40908b61380c1d3",
".git/objects/d4/3532a2348cc9c26053ddb5802f0e5d4b8abc05": "9e0a7dce91540443aeee8c8cd1dcd7df",
".git/objects/7a/6c1911dddaea52e2dbffc15e45e428ec9a9915": "9be869e0f2a532b8d9478d6e87c5651f",
".git/objects/98/0d49437042d93ffa850a60d02cef584a35a85c": "65d18a9837aeb392a90de68393cdffdf",
".git/objects/98/2f4b15250237f2030cba81047b9a0b9bcef5cc": "f7a0974694e8dfaa59af89125807bb8a",
".git/objects/98/64feaf6f3224a594ce292045a6a710fc388da1": "16ea57dcbb1d63f314feafb2a92d0ffa",
".git/objects/98/c5fc903a2c5ca1aa95da92290bc66c55ff94a7": "5072c70fc66fedde2897647088747752",
".git/objects/4d/bf9da7bcce5387354fe394985b98ebae39df43": "0e814c95743da9ea171bf1decf280426",
".git/objects/4d/33f10243e0cd3b87ec7cc3b7fa4a712f9709d2": "12af7aaa6a069d1237eeafc7da055774",
".git/objects/b6/b8806f5f9d33389d53c2868e6ea1aca7445229": "fb4469623771e09349b55fdfac63c384",
".git/objects/9b/3ef5f169177a64f91eafe11e52b58c60db3df2": "3278c0abd1b752fe3fa5b8db850d7d2e",
".git/objects/29/f22f56f0c9903bf90b2a78ef505b36d89a9725": "3a89fbf457bc2e25769ea12fc25cbda0",
".git/objects/ca/3bba02c77c467ef18cffe2d4c857e003ad6d5d": "45334b0198ddc18d9c58f1dd9acbd2d7",
".git/objects/c4/016f7d68c0d70816a0c784867168ffa8f419e1": "0fc0a5b3bd7ab6741d66a114a90cf074",
".git/objects/c4/744a95ca24e385a77592b356a3e45ec1268f6d": "bd4725b06f496af9af5b5a515e290a34",
".git/objects/c4/58d8886b6e140cc9137717efc7648afed56465": "7ab8f465d256535f35f739937155dfef",
".git/objects/ed/b55d4deb8363b6afa65df71d1f9fd8c7787f22": "6d7d63ee29850883161729da29f76056",
".git/objects/20/3a3ff5cc524ede7e585dff54454bd63a1b0f36": "33121d4fda2c7d3071b038d9f116e342",
".git/objects/9e/3b4630b3b8461ff43c272714e00bb47942263e": "d51750c088c514598acbb1e6dec6ee4e",
".git/objects/4f/fbe6ec4693664cb4ff395edf3d949bd4607391": "c51611bb14cd84d8f9410068ad6728af",
".git/objects/4f/04a70d23507cec14ae27ddbf6d7174941ef1ba": "ae409db390ebdaca3d02b40ee198f4d4",
".git/objects/4f/40dabe4430b4d7288c6ae92d8fc3e17cf8e410": "99eca29a61b4d0fb465bf981cd75ff25",
".git/objects/8a/aa46ac1ae21512746f852a42ba87e4165dfdd1": "af85c278711e6c817f328f12fc54f4bc",
".git/objects/8a/85cae1e2ad87dceb06e928fed87dfbd4ff49f8": "eee40313e6110924ddfdb55d035ebbf6",
".git/objects/8a/85d895e9087dc90df693c801a67553704c9f47": "2397a4971f271581d90a70f75db50763",
".git/objects/fe/3b987e61ed346808d9aa023ce3073530ad7426": "0e99633a6098bc621e976bd2117f2be1",
".git/objects/b7/49bfef07473333cf1dd31e9eed89862a5d52aa": "03dc7c3b440d9d36b3e1dc2ab06f364c",
".git/objects/b7/a161d610af89af84d7f687b2e4ef311f569d86": "662a5bc92b30aee25aafb3f83b21b0be",
".git/objects/88/cfd48dff1169879ba46840804b412fe02fefd6": "38c08fdc0dbc026fb372f8f06543db01",
".git/objects/eb/9b4d76e525556d5d89141648c724331630325d": "36264bdfab0a32a2aefdc668e36d971d",
".git/objects/eb/6a0b31b315c9062971eb4e8174040f0668c24c": "8db23b0f10240c5883bc60a257669c55",
".git/objects/d6/9c56691fbdb0b7efa65097c7cc1edac12a6d3e": "3a663110118a15ba1de956e30344cd34",
".git/objects/c9/298c490de6a76dd48c20023cb71abde4884baf": "5b566ab944445887844119347f943c37",
".git/objects/2e/85ee5a35e662de34809227d926c9b33e885805": "81bfab96aa58732c2268b42ae364f662",
".git/objects/bd/4050f1261e4f80269c181621a75a05922535ab": "833cc3af967ce726e13f6ecb757d1938",
".git/objects/c3/e2ee34daf1b1a7fceeefb8ea4d16547fa05ad2": "ef5817c03979f47b24f579bdbd10b159",
".git/objects/c3/30d1e8fc934e4e290befcc141eb9e7dd40cd7a": "0b33db538b49afc1092866a9bce14f04",
".git/objects/c3/7d7baa7d6a1e32d2a68430722d51c38cdf1c5e": "6bf8c1b4c27cd6970bb2555fed18656d",
".git/objects/4c/8662e21acac47809edd54423ae7d930f5084ea": "8066fea301b17c5f30a1f9f254f4d5c0",
".git/objects/4c/8337d7cded0cdbc46a0c158c2df8d75ee93015": "2262e120195666bf4ca1c383ecf63765",
".git/objects/6b/9862a1351012dc0f337c9ee5067ed3dbfbb439": "85896cd5fba127825eb58df13dfac82b",
".git/objects/f5/72b90ef57ee79b82dd846c6871359a7cb10404": "c86893109644c17e9714c1db6ccfdcce",
".git/objects/f5/a66a75a2dccaf0b6de84c6d4d1689d567f0096": "c1595568e2a4abe0702c9359541ddbb1",
".git/objects/79/38f52df34b597404d0034ae3074064bae5de41": "79f0b48b85bd72bec5b9b27fc604f106",
".git/objects/79/94b063c9f81f6d9b176f16acc4ee69f2bffc77": "6dcac8fb9c60e926316f97d787399818",
".git/objects/99/b3a5b6ec66be4cec842529a05d39c4c95d2cfc": "4c3aa1943eec105edd7f1c3d028b05a0",
".git/objects/99/f9cc4ab8a4eae3c55c6f4523b2a0a7c8ee702b": "b635c45a19afd1b25af3c2fbc5101d93",
".git/objects/99/3bd601d02def319c2c457324fc32c24025dae9": "76f2931fdef9739d6dab72397dd347a6",
".git/objects/43/508c0adbf776b3fe429c644ff42d369e5af644": "f903c0af7ea28455c9d2a30d198bb37e",
".git/objects/43/a5770170b3e92a0947d9e608e47f5501fc3f21": "a1928bf808e76eb22a8ec62f74d135cb",
".git/objects/2c/866b484a042f11e73f41c9ad3942d47380d8f9": "8b481f0a744766e6e12dcb4b3f192907",
".git/objects/2c/f5a60f53ac810700565db763d917273a744cc0": "11aff3b6bc9e8358e5acc0a04f06c8d8",
".git/objects/f2/04823a42f2d890f945f70d88b8e2d921c6ae26": "6b47f314ffc35cf6a1ced3208ecc857d",
".git/objects/e3/e9ee754c75ae07cc3d19f9b8c1e656cc4946a1": "14066365125dcce5aec8eb1454f0d127",
".git/objects/02/1d4f3579879a4ac147edbbd8ac2d91e2bc7323": "17f03e66ae7084386b483f2a6fea5e57",
".git/objects/02/c25c38b7822b2abf8c3153cfb0c0d0da0df24a": "647b9c7dd6062c332aaf4cdf18b60c81",
".git/objects/02/e1678211fe4975e3bb9ed867c48902b63d139a": "4ef2a7da157e9321489d6ee9f6f470c9",
".git/objects/b9/2a0d854da9a8f73216c4a0ef07a0f0a44e4373": "f62d1eb7f51165e2a6d2ef1921f976f3",
".git/objects/b9/dfa28958f91162e8fa934a3a74da4eb5ce729d": "f0582e33adff8185f4669579a5efc3d1",
".git/objects/b9/b731a12e134ec21a75902575f7704da5a310d4": "c280ba4e8b2a608eab067e5a25fc131c",
".git/objects/10/565a2751eedcd16078ee1f3862b85d7c2ce5f4": "ebe68e255ddfab9e42c3bf94d48debd9",
".git/objects/10/f14ef3f30b3180de20bfb0cbfe73d460a417a5": "82a6353afad721fc17cc75fa26b25a65",
".git/objects/35/5b08993e3a24d761c86de97db2fc1c52021928": "caa238e57e20511907d95a1aaa518291",
".git/objects/35/6c9c516c8124c7bd67ca6acbceaca789e5ada3": "ba3ba8e7d587de0dba88ce20accd9b78",
".git/objects/35/c092e1e4259e3f7669fc3406da9af7338b80af": "0f13033e433ff6e934ac24b16ede1f80",
".git/objects/f1/f9b680b545f5d3cefd454918907718d1b57310": "734754f72e2183367d90866d156eebec",
".git/objects/2f/10f4f56d991c9ebd91d473bb35f1665459d13c": "7db38d85e5189a3909b5f1b7d84f5df7",
".git/objects/dc/82976e2d953df2826b7d3cd1540f5a3c7cc9f1": "4cce59bc592ec0618fe89dc5526cdc3e",
".git/objects/bf/1b55117b3ba46b9b2fce313f4bbf323761b21b": "8f9bb3f941e3c5c3bc2ccf65fa209be3",
".git/objects/bf/60009ecb8d2bf9494f512e6ca2694daee741ab": "f8f985f38f8580ef44445e98ff2bb053",
".git/objects/bf/310849c93749131f16834b839a0e0a8d76cbba": "99c41e698551be53bff6c6c9ec1dd6e3",
".git/objects/bf/b0c401d2e777b01500b417cea2edb8463a03d7": "0489ca9dff8c61a94a31b69404826b53",
".git/objects/d3/b092d34651bb13354832d33b2bd97351aae8e0": "4503f99470a47a097e03ca0fad618760",
".git/objects/14/fc65065275aeb8990cd4c7bc32eacc0a8d081a": "5358af7eb1abdf3b7708364c7752bf05",
".git/objects/05/4cb8839b5ff62b2af223d22d92990269c6ff48": "af25d97f959333733cc13d742a4a06bb",
".git/objects/05/bd27d0afca13b5b698f06397083977b2fcaa9c": "e700ded5c911795cc13c0d88782ea5e9",
".git/objects/ef/39d31138c93e220f820dec89a3e3f91f2b0a73": "d1d275beb9ebae8d025668095d585a89",
".git/objects/e5/1ca077da995ba5ddc6b652f3ae638a3c00685d": "588efb688990818bef822f5d55db6b68",
".git/objects/e5/1248d8b716316830efcc3a80ff251a5cb2a243": "34c4137827d291ede25f4010e84779b3",
".git/objects/e8/593bcef39af745d7f7e6f0f339c3521a600899": "2ec4385fea61d3b5089bbb3371f0fc9d",
".git/objects/e8/dfc3ffdc64d921749bc4ec104fc11b40b41aae": "b58bc111aa7def51f11c2440b01b4fd1",
".git/objects/a9/9d510c20040ecdb1cd54027f224e1bf5e96651": "63adc4d0696aebae51c040c399115d18",
".git/objects/1a/bbe3d4abadb2179668ea36d7d32a2b77640721": "5d3a5eeb24d6272c37e7e212e3f4fae7",
".git/objects/df/191376a17ca3861081e400659f8efc72a85bdd": "87241833b97b8bc11c8d4413daace099",
".git/objects/df/208766c2270d223cbc3ed9b0c683fd39b970dd": "e05d88aa8da13b53111974e2a16e7ccf",
".git/objects/5d/d4a9ef730439688d1fa0911b972aaa3b22f6ad": "2f415e11773698121ddf19299b4318b9",
".git/objects/fd/00c020a1a04b5703cb529d6ffdb031d3fe69b8": "5a4aeb45c531420c754fffe7b5f4432f",
".git/objects/00/b24661ef943cf8980393d501edab50ee18344c": "27c5a188a14a048d63370c8a1e661596",
".git/objects/78/3652e1f9a6a35d61d78592540787402f868cfb": "7ee8b1cd65011b6af8d8b7d3d1b014f7",
".git/objects/78/3400a5ee2a84b6fbf88caaf64f8fa7072c94f3": "1e9337a7a6361963cfa1797752583617",
".git/objects/78/47f2f7da3b51a45672126659b95a0e046d2471": "f359b22cd796b4164321035f9f89e618",
".git/objects/e2/df487a24acc598100996b3f86f3fc05e9155fe": "eff7e19fc4ba32ed90ac5a25a1e6ac14",
".git/objects/97/cea9649c406a7c71c6b97bd2c1ba10589088b6": "fe625d051ca5535ac314e3e60ea8f034",
".git/objects/aa/cdb95712a53fda4b57c3c061e2815ca5f616b9": "3f40b662090a87c9471ca325de06d151",
".git/objects/a7/0714a3c49fdab3eda9ad218e92fd22f820a989": "75b3472eae87196c8657062eb4ae0d5a",
".git/objects/37/ebe4236efdaacefb93273b389b506825b0482b": "bbf975059d507df1b0c292ecb63b7691",
".git/objects/82/039646eae58381941a128edf3dd254c98a2961": "ed5ffad7ec763c7e7391b0219d94b0ca",
".git/objects/ea/986bbe9983d634ac342f9600d48ac030cf2198": "3feb4c09530b014d15ad3a60ebe5910a",
".git/objects/ea/e83911cadac23e3fd597e72158b58d650a9eaf": "a5a5f1b9c5c1f7f7d762131d5c595607",
".git/objects/81/b9565365b0fe6977f11e4cfdc5c68a47c90cb0": "2b8cceb45b080393f5403ca1c80eaa25",
".git/objects/81/ba5936928e6734f76c83017ac1ff8fcd825880": "3f99bbf5296e793a2024d8be0b0524f0",
".git/objects/b2/7fdd90ab3d1cd8b13667394351b48fbe7f57ef": "cca43b203a4af097711ebfddc3f2308b",
".git/objects/19/6d0937683a69215348d5f5c55913c5a20215f1": "05b0f8a5a01c579dd08aa2d0a5311655",
".git/objects/19/3eef257b03c742a108f43721cc2234758a7b9a": "9313adb6f141034ce478f8dc97dc2a18",
".git/objects/48/680e783eede4bb701ede58f2a303ad436cb3cb": "1423cf3dd6cd7180f14204bbb20974d1",
".git/objects/48/7892a24a6b108e36a1b7b49e0ea105e115ef1f": "54ec797f51c573750fc36409ef0df466",
".git/objects/48/aab133f9ba9d29c5493e72984ca1f894871d5e": "02a5dfdb0da52bbcefaaadb26109be0f",
".git/objects/fa/2d2b4ec825d1626560d22aadca12283e095430": "e8e53746d9cd7c5b6935aa63a5dabfa2",
".git/objects/83/8c87dd07c155f4ad6f02d3a1021b887bbcd997": "09169aff668d598515881c7f013175b8",
".git/objects/4b/3592d66f080e150aed4efadbe25262ac12f01b": "ee7837da6f7d0d8e466ff2e014dce249",
".git/objects/68/ef6030811fc24591f9f0af7b5604c784262520": "34e00f4c6785ffeaa7f24a9b3d86a088",
".git/objects/3f/5e643ae00848880bb8d4542ab8135e7ee09e98": "17f9235f717c8712772f4159db4edc51",
".git/objects/73/dc7e4d5014d48db57339c41ce6db5c1a4e7df7": "26bac0ecb9e7a46fa31fd3ceb9910788",
".git/objects/52/b760185494f54f6ace8bd2f2dc9e86b9e3f067": "b6cf36b1922b22fe4a31f821b436dc8e",
".git/objects/52/b13a3fae014212e7119b8b65f8f54ddc01a75e": "32182948f73d58640176a72098c166a8",
".git/objects/6d/095c2637dd718d83dc4b3d91968105336d1b10": "389e83fde28059fc449025d161fd7bac",
".git/objects/6d/2d0fe0905fb72274227fa7396ce8c20d97aa6d": "d304bba845f7d73fece5581d6824cd03",
".git/objects/be/7c05181718b1e70910ebd0f6be41f7491ff309": "2307162e3749142af57904f980671801",
".git/objects/be/bc52c0aea23764c680e96bc6100712ff2b2839": "12fae15832b5818dea272c3847ea57ab",
".git/objects/1e/07f3082c0d0432d12d2c2ca3a905d89869ba6b": "be484543bdf33effe0fe6697f13a6549",
".git/objects/1e/018aaa6adf42079a39c8db3d0aac6d853f801e": "aad7eef3563fd6638d611e9aaa87325a",
".git/objects/46/97389b95333ed10d8ff50891faa50011f8338d": "f09f58d811939e57622eea9ae9fefbdd",
".git/objects/26/457b502c8a724383b6f8a3937c3330840505fe": "ac0b250bc6c497a10baaed0961466f90",
".git/objects/6e/5f24417ee810f887d6cf6113cc2bd2f4c2214c": "a790ccc2569ed1696b00a2dcee003c3e",
".git/objects/95/84d8f5a66696a530ebe8e7ed3ba38a34f9749d": "2a0faeceb8169f1537c7fc0406391058",
".git/objects/95/4c0e32f4a0887f1b83f66d69beb79131680543": "408458d4f16f700fdc8e9c572528ee2a",
".git/objects/53/9b0b42bc78af193a3f8b5529d7c1b89abf4314": "f20ad7190cd5a6a79629ea95b18339ff",
".git/objects/59/0587415c1ee338196ac4f9258e22705662e4af": "becae95bd48602b3b5c2622d8b2cdd63",
".git/objects/c0/266f99797c99add19f2680f63cbe848a75755f": "f194a5c0a872b5243bec912588e8f7f7",
".git/objects/a1/930c12ed610a147c4b3bb14499378e0dab06d9": "a722620ad263e914468e4c95b56bd965",
".git/objects/0a/2acdf8d194253400dfda82462725b12aae2b71": "d609111aec876a6eda39731e6175d21a",
".git/objects/0a/a454c437fdbd077f7af676ddd3aaaba7d1bf54": "4a0a4c225970f97330d957eab7a9ef14",
".git/objects/07/5927c8312dd5451dbcafbfb81f2878b4d746b2": "d61014aa7a75713e53c1e69044bebc7e",
".git/objects/ac/4437a4c5175e0dd77cc96891029b18b45acb18": "2795db57739861e4edc73dc92e88a819",
".git/objects/ac/2c12c3744eac20ae417f6b4dfba5e50b8da61c": "7202191c569189bd84d3150062710b1d",
".git/objects/8f/372fa753f392e2836eca1f07966a8b1db6c964": "65f6b28ae6dc6962ec89487121035021",
".git/objects/28/318715dbb232bb89a6e64a4afdd12137d0eda1": "bead4507fa2b5064113c39476e7ad154",
".git/objects/a8/b5342d3d372023d9265c1e30e0fb523cc5a67d": "5beda803f84ee53e9f9c89c6696c9a8a",
".git/objects/47/54c1b7de074a9ad7eecfe91c1fe8ba53430754": "1f6fc02523bc28fc52d37a86a5705e1d",
".git/objects/94/d32bd885170b7ede225b1dc208484a9721a7a7": "ea1dc345b3cc17bbc33938534dec0448",
".git/objects/94/0219cbc44396e2dedf70bc4a67bfd929c8eef8": "c6e69fd06ce693691b01ab8d96c93029",
".git/objects/c6/e02644435913b2f8fb3857e64c3295f87385d4": "5ee5ae759e59a866e4e87146e1e1449a",
".git/objects/a3/80721c0662cb562243a51d46aa45a082297790": "795a37d6318c042ed2de3ef684add692",
".git/objects/32/8622c4ca063e5e0229118545ff6e938d89bd59": "8bff908d8dda9d3465dd862ca9f290b1",
".git/objects/50/3ea97cbe43c0fbabe91d5a21e5e2c4753b6628": "935ba16b9d22caad9b06a65d9d99cc5c",
".git/objects/ad/14be9f28e38bc6f0719f718faabd4af6b0c2d1": "a88067a1d270ed130cbcfefa5f17bfb5",
".git/objects/c7/fe850fe690555a2d8b39f066f3750695cfd5dd": "072073dfe0b389f9d7d20b25aac290a2",
".git/objects/a0/cbee06b521c9fc4a2082c977ea5d61d34cdd95": "3803a2b91b83319b128574c29b9b0677",
".git/objects/08/fbcfb5b99ffa85799e3c4439f9aed6d58dc908": "5e0400bf21adfd0e5975991c16763a59",
".git/objects/e1/394f21d1a14f2d5fcde31edce900badc7a2c06": "3c914b0cbda7f023878755ce2b6da523",
".git/objects/84/0609ad801cbdc9aa1358e50494d70b2cb5886f": "96044571c2063cc86c0d2a62846f22cb",
".git/objects/65/94c8d5c86dd3644a40f28b4ea3f9355c8d17fb": "ff233c58d4629c36dccbcabd992c0b28",
".git/objects/2d/511954e067356c973ffc9417207b37114544ae": "596cf1134864cafc6d7ca5d901211adf",
".git/objects/74/9a274b1d0d15047d13556de66cc3b6ca6e384d": "69b6b377fd0bf591c23372f572e2f2fa",
".git/objects/5c/88e50dfb2b20c5ff4c0846e320596407b0eed5": "4849e510bac542bf552e78ca2303cfdb",
".git/COMMIT_EDITMSG": "634861187cd95de4c014e030b56ce12c",
".git/logs/HEAD": "80a0af3c9d75a592a8ec84d7b438e23d",
".git/logs/refs/heads/main": "64d235a313bcbf377bd5a70c50e54ebb",
".git/logs/refs/remotes/origin/main": "a4c6138eb2e2808a636eaaf5fe842acc",
".git/logs/refs/remotes/origin/HEAD": "b5a3ccdd8d2dce8bd59a9831bf21f82a",
".git/HEAD": "cf7dd3ce51958c5f13fece957cc417fb",
".git/FETCH_HEAD": "de58f1022b12f604c2387e7a68d77969",
".git/ORIG_HEAD": "efd960e0ae6d403f3f9e1d968b170999",
".git/index": "89db5c5bab7880a41d5fa3e82cecdfc3",
".git/config": "2a808f48e10ce8660f7044bbe87a2be3",
"CNAME": "c2d7c369bc6f93bc2255a70b40351311"};
// The application shell files that are downloaded before a service worker can
// start.
const CORE = ["main.dart.js",
"index.html",
"flutter_bootstrap.js",
"assets/AssetManifest.bin.json",
"assets/FontManifest.json"];

// During install, the TEMP cache is populated with the application shell files.
self.addEventListener("install", (event) => {
  self.skipWaiting();
  return event.waitUntil(
    caches.open(TEMP).then((cache) => {
      return cache.addAll(
        CORE.map((value) => new Request(value, {'cache': 'reload'})));
    })
  );
});
// During activate, the cache is populated with the temp files downloaded in
// install. If this service worker is upgrading from one with a saved
// MANIFEST, then use this to retain unchanged resource files.
self.addEventListener("activate", function(event) {
  return event.waitUntil(async function() {
    try {
      var contentCache = await caches.open(CACHE_NAME);
      var tempCache = await caches.open(TEMP);
      var manifestCache = await caches.open(MANIFEST);
      var manifest = await manifestCache.match('manifest');
      // When there is no prior manifest, clear the entire cache.
      if (!manifest) {
        await caches.delete(CACHE_NAME);
        contentCache = await caches.open(CACHE_NAME);
        for (var request of await tempCache.keys()) {
          var response = await tempCache.match(request);
          await contentCache.put(request, response);
        }
        await caches.delete(TEMP);
        // Save the manifest to make future upgrades efficient.
        await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
        // Claim client to enable caching on first launch
        self.clients.claim();
        return;
      }
      var oldManifest = await manifest.json();
      var origin = self.location.origin;
      for (var request of await contentCache.keys()) {
        var key = request.url.substring(origin.length + 1);
        if (key == "") {
          key = "/";
        }
        // If a resource from the old manifest is not in the new cache, or if
        // the MD5 sum has changed, delete it. Otherwise the resource is left
        // in the cache and can be reused by the new service worker.
        if (!RESOURCES[key] || RESOURCES[key] != oldManifest[key]) {
          await contentCache.delete(request);
        }
      }
      // Populate the cache with the app shell TEMP files, potentially overwriting
      // cache files preserved above.
      for (var request of await tempCache.keys()) {
        var response = await tempCache.match(request);
        await contentCache.put(request, response);
      }
      await caches.delete(TEMP);
      // Save the manifest to make future upgrades efficient.
      await manifestCache.put('manifest', new Response(JSON.stringify(RESOURCES)));
      // Claim client to enable caching on first launch
      self.clients.claim();
      return;
    } catch (err) {
      // On an unhandled exception the state of the cache cannot be guaranteed.
      console.error('Failed to upgrade service worker: ' + err);
      await caches.delete(CACHE_NAME);
      await caches.delete(TEMP);
      await caches.delete(MANIFEST);
    }
  }());
});
// The fetch handler redirects requests for RESOURCE files to the service
// worker cache.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== 'GET') {
    return;
  }
  var origin = self.location.origin;
  var key = event.request.url.substring(origin.length + 1);
  // Redirect URLs to the index.html
  if (key.indexOf('?v=') != -1) {
    key = key.split('?v=')[0];
  }
  if (event.request.url == origin || event.request.url.startsWith(origin + '/#') || key == '') {
    key = '/';
  }
  // If the URL is not the RESOURCE list then return to signal that the
  // browser should take over.
  if (!RESOURCES[key]) {
    return;
  }
  // If the URL is the index.html, perform an online-first request.
  if (key == '/') {
    return onlineFirst(event);
  }
  event.respondWith(caches.open(CACHE_NAME)
    .then((cache) =>  {
      return cache.match(event.request).then((response) => {
        // Either respond with the cached resource, or perform a fetch and
        // lazily populate the cache only if the resource was successfully fetched.
        return response || fetch(event.request).then((response) => {
          if (response && Boolean(response.ok)) {
            cache.put(event.request, response.clone());
          }
          return response;
        });
      })
    })
  );
});
self.addEventListener('message', (event) => {
  // SkipWaiting can be used to immediately activate a waiting service worker.
  // This will also require a page refresh triggered by the main worker.
  if (event.data === 'skipWaiting') {
    self.skipWaiting();
    return;
  }
  if (event.data === 'downloadOffline') {
    downloadOffline();
    return;
  }
});
// Download offline will check the RESOURCES for all files not in the cache
// and populate them.
async function downloadOffline() {
  var resources = [];
  var contentCache = await caches.open(CACHE_NAME);
  var currentContent = {};
  for (var request of await contentCache.keys()) {
    var key = request.url.substring(origin.length + 1);
    if (key == "") {
      key = "/";
    }
    currentContent[key] = true;
  }
  for (var resourceKey of Object.keys(RESOURCES)) {
    if (!currentContent[resourceKey]) {
      resources.push(resourceKey);
    }
  }
  return contentCache.addAll(resources);
}
// Attempt to download the resource online before falling back to
// the offline cache.
function onlineFirst(event) {
  return event.respondWith(
    fetch(event.request).then((response) => {
      return caches.open(CACHE_NAME).then((cache) => {
        cache.put(event.request, response.clone());
        return response;
      });
    }).catch((error) => {
      return caches.open(CACHE_NAME).then((cache) => {
        return cache.match(event.request).then((response) => {
          if (response != null) {
            return response;
          }
          throw error;
        });
      });
    })
  );
}
