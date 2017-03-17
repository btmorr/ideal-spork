# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure configures the configuration version
# (we support older styles for backwards compatibility). Please don't change it unless you know what you're doing.
Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  config.vm.box = "ubuntu/xenial64"
  config.vm.box_version = "20170316.3.0"

  # Must install: vagrant plugin install vagrant-hostmanager
  config.hostmanager.enabled = true

  project_name = File.basename(Dir.getwd)

  config.vm.define project_name do |node|
    node.vm.network :private_network, :ip => '192.168.121.10'
    config.vm.synced_folder './provisioning', "/home/vagrant/#{project_name}"
    config.vm.provision :ansible do |ansible|
      ansible.playbook = './build.yml'
      ansible.inventory_path = './hosts'
      ansible.host_key_checking = false
      ansible.extra_vars = {
        ansible_ssh_user: 'vagrant',
        sudo: true
      }
    end

  end

  config.vm.define 'zk1' do |node|
    node.vm.network :private_network, :ip => '192.168.121.11'
    config.vm.host_name = 'zk1'
  end

  config.vm.define 'kafka1' do |node|
    node.vm.network :private_network, :ip => '192.168.121.12'
    config.vm.host_name = 'kafka1'
  end

  config.vm.define 'cass1' do |node|
    node.vm.network :private_network, :ip => '192.168.121.13'
    config.vm.host_name = 'cass1'
  end

  config.vm.define 'spark1' do |node|
    node.vm.network :private_network, :ip => '192.168.121.14'
    config.vm.host_name = 'spark1'
  end

  config.vm.define 'standford1' do |node|
    node.vm.network :private_network, :ip => '192.168.121.15'
    config.vm.host_name = 'stanford1'
  end

  config.vm.provision :ansible do |ansible|
    ansible.playbook = './vagrant-ansible.yml'
    ansible.extra_vars = {
      ansible_ssh_user: 'vagrant',
      sudo: true
    }
    ansible.host_key_checking = false
    ansible.groups = {
      'zookeeper' => [ 'zk1' ],
      'kafka'     => [ 'kafka1' ],
      'cassandra' => [ 'cass1' ],
      'spark'     => [ 'spark1' ],
      'stanford'  => [ 'stanford1' ],
    }

  end

end
