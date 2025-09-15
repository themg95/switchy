{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs =
    { self, nixpkgs }:
    let
      forAllSystems = nixpkgs.lib.genAttrs nixpkgs.lib.systems.flakeExposed;
    in
    {
      devShells = forAllSystems (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
          libs = with pkgs; [
            libpulseaudio
            libGL
            glfw
            openal
            stdenv.cc.cc.lib
            jdk21
          ];
        in
        {
          default = pkgs.mkShell {
            packages = [ ];
            buildInputs = libs;
            LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libs;
            JAVA_HOME_17 = "${pkgs.jdk21}/lib/openjdk";
          };
        }
      );
    };
}
