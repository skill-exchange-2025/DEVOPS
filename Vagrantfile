# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # Use a valid Vagrant box
  config.vm.box = "ubuntu/bionic64"  # Replace "base" with a valid box

  # Optional: You can adjust memory if needed
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "1024"  # Adjust memory size if needed
  end

  # Optional: You can add forwarded ports if you need to access services
  # config.vm.network "forwarded_port", guest: 80, host: 8080

  # Optional: Sync folders between host and guest machine
  # config.vm.synced_folder "./data", "/vagrant_data"

  # Optional: Enable provisioning (e.g., install packages)
  # config.vm.provision "shell", inline: <<-SHELL
  #   apt-get update
  #   apt-get install -y apache2
  # SHELL
end
